package com.career.analytics.exception;

public class CoverLetterException extends RuntimeException {
    public CoverLetterException(String message) {
        super(message);
    }

    public CoverLetterException(String message, Throwable cause) {
      super(message, cause);
    }
    public CoverLetterException(Long id) {
      super(" 자기소개서를 찾을 수 없습니다.ID : " + id);
    }
}
