package com.sunstriker;

import com.sun.net.httpserver.HttpServer;
import com.sunstriker.handlers.RoleHttpHandler;
import com.sunstriker.handlers.UserHttpHandler;
import com.sunstriker.handlers.UserRoleHttpHandler;

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

        httpServer.createContext("/user", new UserHttpHandler());
        httpServer.createContext("/role", new RoleHttpHandler());
        httpServer.createContext("/user/roles", new UserRoleHttpHandler());

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
