package com.sunstriker;

import com.sun.net.httpserver.HttpServer;
import com.sunstriker.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServerStarter{
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(Integer.parseInt(resourceBundle.getString("port"))), 0);

        // create user, delete user
        httpServer.createContext("/user", new UserHttpHandler());
        // create role, delete role
        httpServer.createContext("/role", new RoleHttpHandler());
        // add role to user, check role
        httpServer.createContext("/user/role", new UserRoleHttpHandler());
        // authenticate
        httpServer.createContext("/user/auth", new AuthHandler());
        // invalidate
        httpServer.createContext("/user/invalidate", new InvalidateHandler());
        // all roles
        httpServer.createContext("/user/allRoles", new AllUserRolesHttpHandler());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                12,
                24,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
        httpServer.setExecutor(threadPoolExecutor);

        httpServer.start();
    }
}
