package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.services.AuthenticationService;
import com.sunstriker.services.UserRoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllUserRolesHttpHandler extends BaseHttpHandler{
    UserRoleService userRoleService;
    AuthenticationService authenticationService;

    public AllUserRolesHttpHandler() {
        super();
        userRoleService = new UserRoleService();
        authenticationService = new AuthenticationService();
    }

    @Override
    protected Object get(HashMap<String, String> params, Headers headers) throws UnauthorizedException {
        // auth verify
        List<String> tokens = headers.getOrDefault("Authorization", new ArrayList<>());
        if (tokens.isEmpty()) throw new UnauthorizedException();
        User user = authenticationService.verifyToken(tokens.get(0));
        return userRoleService.getAllRoles(user);
    }
}
