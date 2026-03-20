package com.career.analytics.exception;

public class WorkExperienceNotFoundException extends RuntimeException {
    public WorkExperienceNotFoundException(String message) {
        super(message);
    }

    public WorkExperienceNotFoundException(Long workExperienceId) {
        super("경력 정보를 찾을 수 없습니다. ID: " + workExperienceId);
    }

    public WorkExperienceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
