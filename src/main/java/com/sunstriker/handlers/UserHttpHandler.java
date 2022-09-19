package com.sunstriker.handlers;


import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.services.UserRoleService;

import java.util.HashMap;

public class UserHttpHandler extends BaseHttpHandler {

    UserRoleService userRoleService;

    public UserHttpHandler(){
        super();
        userRoleService = new UserRoleService();
    }
    @Override
    protected Object post(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String username = formData.getOrDefault("username", "");
        String password = formData.getOrDefault("password", "");
        if(username.isEmpty() || password.isEmpty()) throw new BadRequestException();

        userRoleService.createUser(username, password);
        return "OK";
    }

    @Override
    protected Object delete(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String username = formData.getOrDefault("username","");
        if(username.isEmpty()) throw new BadRequestException();

        userRoleService.deleteUser(username);
        return "OK";
    }


}
