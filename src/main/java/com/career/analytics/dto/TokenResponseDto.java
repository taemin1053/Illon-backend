package com.career.analytics.dto;

import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String accessToken;
    private final String tokenType = "Bearer";
    private final Long userId;
    private final String email;
    private final String name; // profile의 name
    private final String role;

    public TokenResponseDto(String accessToken, Long userId, String email, String name, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
