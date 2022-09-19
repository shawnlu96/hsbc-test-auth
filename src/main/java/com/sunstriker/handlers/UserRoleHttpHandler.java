package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.services.UserRoleService;

import java.util.HashMap;

public class UserRoleHttpHandler extends BaseHttpHandler{
    UserRoleService userRoleService;

    public UserRoleHttpHandler(){
        super();
        userRoleService = new UserRoleService();
    }

    @Override
    protected Object put(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String username = formData.getOrDefault("username", "");
        String roleName = formData.getOrDefault("roleName", "");
        if(username.isEmpty() || roleName.isEmpty()) throw new BadRequestException();
        userRoleService.addUserRole(username, roleName);
        return "OK";
    }
}
