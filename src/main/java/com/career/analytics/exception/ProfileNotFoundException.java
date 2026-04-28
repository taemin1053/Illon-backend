package com.career.analytics.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {

        super(message);
    }
    public ProfileNotFoundException(Long userId) {
        super("프로필을 찾을 수 없습니다. User ID: " + userId);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}


