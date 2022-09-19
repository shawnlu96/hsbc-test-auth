package com.sunstriker.services;

import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.models.domains.Role;
import com.sunstriker.models.domains.User;
import com.sunstriker.models.domains.UserRole;
import com.sunstriker.storage.Storage;

public class UserRoleService {

    public void createUser(String username, String password) throws ForbiddenException {
        User newUser = new User(username, password);
        if(!newUser.save()) throw new ForbiddenException("User already exists.");
    }

    public void deleteUser(String username) throws ForbiddenException {
        User user = Storage.getInstance().userMap.remove(username);
        if(user == null) throw new ForbiddenException("User does not exist.");
        user.onRemoved();
    }

    public void createRole(String roleName) throws ForbiddenException {
        Role newRole = new Role(roleName);
        if(!newRole.save()) throw new ForbiddenException("Role already exists.");
    }

    public void deleteRole(String roleName) throws ForbiddenException{
        Role role = Storage.getInstance().roleMap.remove(roleName);
        if(role == null) throw new ForbiddenException("Role does not exist.");
        role.onRemoved();
    }

    public void addUserRole(String username, String roleName) throws ForbiddenException {
        // lock the hashSet in case deletion of user or role happens after the containsKey()s and before the save()
        synchronized (Storage.getInstance().userRoles){
            if(!Storage.getInstance().userMap.containsKey(username)) throw new ForbiddenException("User does not exist.");
            if(!Storage.getInstance().roleMap.containsKey(roleName)) throw new ForbiddenException("Role does not exist.");
            UserRole newUserRole = new UserRole(username, roleName);
            newUserRole.save();
        }
    }

    public boolean checkRole(User user, String roleName) throws ForbiddenException {
        synchronized (Storage.getInstance().userRoles){
            if(!Storage.getInstance().roleMap.containsKey(roleName)) throw new ForbiddenException("Role does not exist.");
            return Storage.getInstance().userRoles.contains(new UserRole(user.getUsername(), roleName));
        }
    }

    public String[] getAllRoles(User user){
        synchronized (Storage.getInstance().userRoles){
            return Storage.getInstance().userRoles.stream().filter(userRole -> userRole.getUsername().equals(user.getUsername())).map(UserRole::getRoleName).toArray(String[]::new);
        }
    }
}
