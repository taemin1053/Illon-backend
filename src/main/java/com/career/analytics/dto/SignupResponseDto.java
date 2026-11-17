package com.career.analytics.dto;

import java.time.LocalDateTime;

public record SignupResponseDto(
        String email,

        LocalDateTime createdAt
) {}
