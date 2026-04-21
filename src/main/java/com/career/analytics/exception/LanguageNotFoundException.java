package com.career.analytics.exception;

public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(String message) {
        super(message);
    }

    public LanguageNotFoundException(Long languageId) {
        super("어학 정보를 찾을 수 없습니다. ID : " + languageId);
    }
    public LanguageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
