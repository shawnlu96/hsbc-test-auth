package com.sunstriker.models.domains;

import com.sunstriker.storage.Storage;

public class UserRole {
    private String username;
    private String roleName;

    public UserRole(String username, String roleName) {
        this.username = username;
        this.roleName = roleName;
    }

    public void save(){
        // HashSet itself provides idempotence
        Storage.getInstance().userRoles.add(this);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!obj.getClass().equals(getClass())) return false;
        return  ((UserRole) obj).getRoleName().equals(roleName) && ((UserRole) obj).getUsername().equals(username);
    }

    @Override
    public int hashCode() {
        return String.format("%s_%s", username, roleName).hashCode();
    }
}
