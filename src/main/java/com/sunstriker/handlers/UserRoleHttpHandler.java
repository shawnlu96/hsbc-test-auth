package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.services.AuthenticationService;
import com.sunstriker.services.UserRoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserRoleHttpHandler extends BaseHttpHandler {
    UserRoleService userRoleService;
    AuthenticationService authenticationService;

    public UserRoleHttpHandler() {
        super();
        userRoleService = new UserRoleService();
        authenticationService = new AuthenticationService();
    }

    @Override
    protected Object put(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String username = formData.getOrDefault("username", "");
        String roleName = formData.getOrDefault("roleName", "");
        if (username.isEmpty() || roleName.isEmpty()) throw new BadRequestException();
        userRoleService.addUserRole(username, roleName);
        return "OK";
    }

    @Override
    protected Object get(HashMap<String, String> params, Headers headers) throws BadRequestException, ForbiddenException, UnauthorizedException {
        // params check
        String roleName = params.getOrDefault("roleName", "");
        if(roleName.isEmpty()) throw new BadRequestException();
        // auth verify
        List<String> tokens = headers.getOrDefault("Authorization", new ArrayList<>());
        if (tokens.isEmpty()) throw new UnauthorizedException();
        User user = authenticationService.verifyToken(tokens.get(0));
        return userRoleService.checkRole(user, roleName);
    }
}
