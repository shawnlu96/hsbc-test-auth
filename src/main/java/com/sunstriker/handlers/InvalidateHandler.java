package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.services.AuthenticationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvalidateHandler extends BaseHttpHandler {
    AuthenticationService authenticationService;

    public InvalidateHandler() {
        super();
        authenticationService = new AuthenticationService();
    }

    @Override
    protected Object post(HashMap<String, String> formData, Headers headers) {
        try {
            List<String> tokens  = headers.getOrDefault("Authorization", new ArrayList<>());
            if(!tokens.isEmpty()) {
                String token = tokens.get(0);
                User user = authenticationService.verifyToken(token);
                // refresh user-specific token secret, so that all token generated before will no longer be valid
                user.refreshTokenSecret();
            }
        } catch (UnauthorizedException e) {
            // ignore exception handling and returns nothing as specified...
        }
        return null;
    }
}
