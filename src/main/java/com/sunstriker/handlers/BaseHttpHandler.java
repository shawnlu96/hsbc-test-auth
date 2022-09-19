package com.sunstriker.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sunstriker.exceptions.BadRequestException;
import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.responses.ErrorResponse;
import com.sunstriker.models.responses.OkResponse;
import com.sunstriker.utils.json.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        Object response = "";
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
                    throw new BadRequestException();
            }

            handleOkResponse(exchange, response);

        } catch (Exception e) {
            handleErrorResponse(exchange, e);
        }
    }


    protected Object get(HashMap<String, String> params, Headers headers) throws BadRequestException, ForbiddenException, UnauthorizedException {
        throw new BadRequestException();
    }

    private Object processGet(HttpExchange httpExchange) throws Exception {
        return get(resolveUrlEncodedData(httpExchange.getRequestURI().getQuery()), httpExchange.getRequestHeaders());
    }

    protected Object post(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        throw new BadRequestException();
    }

    private Object processPost(HttpExchange httpExchange) throws Exception {
        return post(resolveUrlEncodedData(getFormDataString(httpExchange)), httpExchange.getRequestHeaders());
    }

    protected Object put(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        throw new BadRequestException();
    }

    private Object processPut(HttpExchange httpExchange) throws Exception {
        return put(resolveUrlEncodedData(getFormDataString(httpExchange)), httpExchange.getRequestHeaders());
    }

    protected Object delete(HashMap<String, String> formData, Headers headers) throws BadRequestException, ForbiddenException {
        throw new BadRequestException();
    }

    private Object processDelete(HttpExchange httpExchange) throws Exception {
        return delete(resolveUrlEncodedData(getFormDataString(httpExchange)), httpExchange.getRequestHeaders());
    }


    private void handleOkResponse(HttpExchange httpExchange, Object payload) throws IOException {
        OkResponse response = new OkResponse(payload);
        byte[] responseContentByte = JsonUtils.convert(response).getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().add("Content-Type:", "application/json; charset=utf-8");

        httpExchange.sendResponseHeaders(200, responseContentByte.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }

    /**
     * handles error response
     *
     * @param httpExchange http exchange
     * @param exception    exception caught
     */
    private void handleErrorResponse(HttpExchange httpExchange, Exception exception) throws IOException {
        int rCode = 0;

        if (BadRequestException.class.equals(exception.getClass())) rCode = 400;
        else if (UnauthorizedException.class.equals(exception.getClass())) rCode = 401;
        else if (ForbiddenException.class.equals(exception.getClass())) rCode = 403;
        else rCode = 500;

        httpExchange.getResponseHeaders().add("Content-Type:", "application/json; charset=utf-8");
        httpExchange.sendResponseHeaders(rCode, 0);
        // if exception is not caught, output common error message.
        ErrorResponse response = new ErrorResponse(rCode == 500 ? "Internal server error." : exception.getMessage());
        byte[] responseContentByte = JsonUtils.convert(response).getBytes(StandardCharsets.UTF_8);
        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }

    // only accepts KVs formed as <String, String>, no complex object resolving included
    private HashMap<String, String> resolveUrlEncodedData(String param) throws BadRequestException {
        HashMap<String, String> res = null;
        try {
            res = new HashMap<>();
            if (param == null || param.isEmpty()) return res;
            String[] pairs = param.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                res.put(kv[0], kv[1]);
            }
        } catch (Exception e) {
            throw new BadRequestException();
        }
        return res;
    }

    private String getFormDataString(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
            sb.append((char) i);
        }
        return sb.toString();
    }

}
