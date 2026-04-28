package com.career.analytics.exception;

public class AwardNotFoundException extends RuntimeException {
    public AwardNotFoundException(String message) {
        super(message);
    }

    public AwardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public AwardNotFoundException(Long awardId) {
        super("수상 정보를 찾을 수 없습니다. ID : " + awardId);
    }
}
