package com.career.analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String accessToken;
    private final String tokenType = "Bearer";
    @JsonIgnore
    private final String refreshToken;
    private final Long userId;
    private final String email;
    private final String name; // profile의 name
    private final String role;
    private final boolean isSurveyCompleted;
    public TokenResponseDto(String accessToken,String refreshToken, Long userId, String email, String name, String role, boolean isSurveyCompleted) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.isSurveyCompleted = isSurveyCompleted;
    }
}
