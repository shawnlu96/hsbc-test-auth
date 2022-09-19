package com.sunstriker.handlers;


import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.services.UserRoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHttpHandler extends BaseHttpHandler {

    UserRoleService userRoleService;

    public UserHttpHandler(){
        super();
        userRoleService = new UserRoleService();
    }
    @Override
    protected String post(HashMap<String, List<String>> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        List<String> usernames = formData.getOrDefault("username", new ArrayList<>());
        List<String> passwords = formData.getOrDefault("password", new ArrayList<>());
        if(usernames.isEmpty() || passwords.isEmpty()) throw new BadRequestException();
        String username  = usernames.get(0), password = passwords.get(0);
        boolean result = userRoleService.createUser(username, password);
        if(!result) throw new ForbiddenException("User already exists.");
        return "OK";
    }

    @Override
    protected String delete(HashMap<String, List<String>> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        List<String> usernames = formData.getOrDefault("username", new ArrayList<>());
        if(usernames.isEmpty()) throw new BadRequestException();
        String username = usernames.get(0);
        boolean result = userRoleService.deleteUser(username);
        if(!result) throw new ForbiddenException("User does not exist.");
        return "OK";
    }
}
