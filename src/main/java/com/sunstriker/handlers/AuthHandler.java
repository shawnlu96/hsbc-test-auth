package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.models.vos.AuthPayload;
import com.sunstriker.services.AuthenticationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthHandler extends BaseHttpHandler {
    AuthenticationService authenticationService;

    public AuthHandler() {
        super();
        authenticationService = new AuthenticationService();
    }

    @Override
    protected Object get(HashMap<String, String> params, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String username = params.getOrDefault("username", "");
        String password = params.getOrDefault("password", "");
        if (username.isEmpty() || password.isEmpty()) throw new BadRequestException();

        return authenticationService.authenticate(username, password);
    }
}
