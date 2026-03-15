package com.career.analytics.exception;

public class EducationNotFoundException extends RuntimeException {

    public EducationNotFoundException(String message) {
        super(message);
    }

    public EducationNotFoundException(Long educationId) {
        super("학력 정보를 찾을 수 없습니다. ID: " + educationId);
    }

    public EducationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
