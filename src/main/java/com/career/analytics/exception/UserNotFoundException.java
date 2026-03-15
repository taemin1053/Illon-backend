package com.career.analytics.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {

        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("사용자를 찾을 수 없습니다. ID: " + userId);
    }
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

