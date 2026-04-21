package com.career.analytics.dto;

import java.time.LocalDateTime;

public record SignupResponseDto(
        String email,
        String name,
        LocalDateTime createdAt
) {}
