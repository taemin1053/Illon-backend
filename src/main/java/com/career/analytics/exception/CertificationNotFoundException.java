package com.career.analytics.exception;

public class CertificationNotFoundException extends RuntimeException {
    public CertificationNotFoundException(String message) {
        super(message);
    }
    public CertificationNotFoundException(Long certificationId) {
        super("자격증 정보를 찾을 . 없습니다. ID : " + certificationId);
    }
    public CertificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
