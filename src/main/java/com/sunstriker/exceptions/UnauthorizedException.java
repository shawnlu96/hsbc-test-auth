package com.sunstriker.exceptions;

public class UnauthorizedException extends Exception{
    @Override
    public String getMessage() {
        return "Unauthorized.";
    }
}
