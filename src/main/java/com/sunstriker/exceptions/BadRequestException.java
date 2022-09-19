package com.sunstriker.exceptions;

public class BadRequestException extends Exception{
    @Override
    public String getMessage() {
        return "Bad request.";
    }
}
