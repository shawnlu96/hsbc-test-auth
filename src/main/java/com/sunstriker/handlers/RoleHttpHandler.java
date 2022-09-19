package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.services.UserRoleService;

import java.util.HashMap;

public class RoleHttpHandler extends BaseHttpHandler {
    UserRoleService userRoleService;

    public RoleHttpHandler() {
        super();
        userRoleService = new UserRoleService();
    }

    @Override
    protected Object post(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String roleName = formData.getOrDefault("roleName", "");
        if (roleName.isEmpty()) throw new BadRequestException();

        userRoleService.createRole(roleName);
        return "OK";
    }

    @Override
    protected Object delete(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        // params check
        String roleName = formData.getOrDefault("roleName", "");
        if (roleName.isEmpty()) throw new BadRequestException();

        userRoleService.deleteRole(roleName);
        return "OK";
    }
}
