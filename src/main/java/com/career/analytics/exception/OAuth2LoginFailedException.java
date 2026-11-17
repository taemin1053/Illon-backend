package com.career.analytics.exception;

public class OAuth2LoginFailedException extends RuntimeException {

    public OAuth2LoginFailedException(String message) {
        super(message);
    }

    public OAuth2LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}