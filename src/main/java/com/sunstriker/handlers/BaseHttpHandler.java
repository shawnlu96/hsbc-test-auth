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
                    throw new BadRequestException();
            }

            handleOkResponse(exchange, response);

        } catch (Exception e) {
            handleErrorResponse(exchange,e);
        }
    }


    protected String get(HashMap<String, List<String>> params, Headers headers) throws BadRequestException {
        throw new BadRequestException();
    }

    private String processGet(HttpExchange httpExchange) throws Exception {
        return get(resolveUrlEncodedData(httpExchange.getRequestURI().getQuery()), httpExchange.getRequestHeaders());
    }

    protected String post(HashMap<String, List<String>> formData, Headers headers) throws BadRequestException, ForbiddenException {
        throw new BadRequestException();
    }

    private String processPost(HttpExchange httpExchange) throws Exception {
        return post(resolveUrlEncodedData(getFormDataString(httpExchange)), httpExchange.getRequestHeaders());
    }

    protected String put(HashMap<String, List<String>> formData, Headers headers) throws BadRequestException {
        throw new BadRequestException();
    }

    protected String processPut(HttpExchange httpExchange) throws Exception {
        return put(resolveUrlEncodedData(getFormDataString(httpExchange)), httpExchange.getRequestHeaders());
    }

    protected String delete(HashMap<String, List<String>> formData, Headers headers) throws BadRequestException, ForbiddenException {
        throw new BadRequestException();
    }

    protected String processDelete(HttpExchange httpExchange) throws Exception {
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
    private HashMap<String, List<String>> resolveUrlEncodedData(String param) {
        HashMap<String, List<String>> res = new HashMap<>();
        if (param == null || param.isEmpty()) return res;
        String[] pairs = param.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            res.putIfAbsent(kv[0], new ArrayList<>());
            res.get(kv[0]).add(kv[1]);
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
