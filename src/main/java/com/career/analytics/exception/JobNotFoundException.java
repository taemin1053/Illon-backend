package com.career.analytics.exception;

public class JobNotFoundException extends RuntimeException {

    // 매개변수가 String인 생성자는 이것 하나로 통일합니다.
    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}