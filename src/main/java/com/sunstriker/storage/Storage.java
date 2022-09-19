package com.sunstriker.storage;

import com.sunstriker.models.domains.Role;
import com.sunstriker.models.domains.User;
import com.sunstriker.models.domains.UserRole;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * singleton memory storage class which mocks persistence level.
 */
public class Storage {
    private volatile static Storage instance;

    public final ConcurrentHashMap<String, User> userMap;
    public final ConcurrentHashMap<String, Role> roleMap;
    public final LinkedList<UserRole> userRoles;

    private Storage(){
        userMap = new ConcurrentHashMap<>();
        roleMap = new ConcurrentHashMap<>();
        userRoles = new LinkedList<>();
    }

    public static Storage getInstance(){
        if(instance == null){
            synchronized (Storage.class){
                if(instance == null) instance = new Storage();
            }
        }
        return instance;
    }
}
