package com.sunstriker.services;

import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.models.domains.User;
import com.sunstriker.storage.Storage;

public class UserRoleService {

    public boolean createUser(String username, String password){
        User newUser = new User(username, password);
        return newUser.save();
    }

    public boolean deleteUser(String username) {
        User user = Storage.getInstance().userMap.remove(username);
        if(user == null) return false;
        user.onRemoved();
        return true;

    }
}
