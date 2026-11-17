package com.career.analytics.exception;

public class ResumeNotFoundException extends RuntimeException {
    public ResumeNotFoundException(String message) {
        super(message);
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ResumeNotFoundException(Throwable cause) {
        super(cause);
    }
    public ResumeNotFoundException(Long resumeId) {
        super("해당 이력서를 찾을 수 없습니다. Resume ID : " + resumeId);
    }
}
