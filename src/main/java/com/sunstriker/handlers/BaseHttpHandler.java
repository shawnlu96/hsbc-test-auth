package com.sunstriker.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sunstriker.exceptions.MethodNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String response = "";
        try {
            switch (requestMethod) {
                case "GET":
                    response = processGet(exchange);
                    break;
                case "POST":
                    response = processPost(exchange);
                    break;
                case "PUT":
                    response = processPut(exchange);
                    break;
                case "DELETE":
                    response = processDelete(exchange);
                    break;
                default:
                    throw new MethodNotSupportedException();
            }
        } catch (MethodNotSupportedException e) {
            handle405Response(exchange);
        }

        handleOkResponse(exchange, response);
    }

    protected String processGet(HttpExchange httpExchange) throws MethodNotSupportedException {
        throw new MethodNotSupportedException();
    }

    protected String processPost(HttpExchange httpExchange) throws MethodNotSupportedException {
        throw new MethodNotSupportedException();
    }

    protected String processPut(HttpExchange httpExchange) throws MethodNotSupportedException {
        throw new MethodNotSupportedException();
    }

    protected String processDelete(HttpExchange httpExchange) throws MethodNotSupportedException {
        throw new MethodNotSupportedException();
    }


    private void handleOkResponse(HttpExchange httpExchange, String response) throws IOException {
        byte[] responseContentByte = response.getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().add("Content-Type:", "application/json; charset=utf-8");

        httpExchange.sendResponseHeaders(200, responseContentByte.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }

    private void handle405Response(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type:", "application/json; charset=utf-8");
        httpExchange.sendResponseHeaders(405, 0);

        OutputStream out = httpExchange.getResponseBody();
        out.write(new byte[0]);
        out.flush();
        out.close();
    }
}
