package com.career.analytics.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@Builder
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String error;
    private Map<String, String> errors; //필드별 에러 메세지
}
