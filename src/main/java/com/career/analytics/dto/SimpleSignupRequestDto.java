package com.career.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "간편 가입 요청 객체")
public record SimpleSignupRequestDto(
        @Schema(description = "포트원 본인인증 ID", example = "verify-1778670245120")
        String verificationId
) {}