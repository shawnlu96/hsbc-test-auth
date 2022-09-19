package com.sunstriker.models.domains;

import com.sunstriker.storage.Storage;

public class Role {
    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean save(){
        Role res = Storage.getInstance().roleMap.putIfAbsent(roleName, this);
        return res == null;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void onRemoved() {
        synchronized (Storage.getInstance().userRoles){
            Storage.getInstance().userRoles.removeIf(userRole -> userRole.getRoleName().equals(roleName));
        }
    }
}
