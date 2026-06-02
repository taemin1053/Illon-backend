package com.career.analytics.controller;

import com.career.analytics.dto.*;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "인증(Auth) API", description = "회원가입, 로그인, 로그아웃 등 보안 및 토큰 관리 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(
        origins = {
                "https://illo-on-sys6.vercel.app",
                "https://illoon.cloud",
                "http://localhost:3000"
        },
        allowCredentials = "true",
        maxAge = 3600
)
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 회원가입", description = "본인인증 ID와 이메일, 비밀번호를 받아 회원가입을 진행하고 즉시 토큰을 발급합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 및 토큰 발급 성공")
    @PostMapping("/signup/email")
    public ResponseEntity<TokenResponseDto> emailSignup(@Valid @RequestBody SignupRequestDto signupDto) {
        // TokenResponseDto를 받도록 수정되었습니다.
        TokenResponseDto response = authService.emailSignup(signupDto, signupDto.getVerificationId());

        // 로그인과 동일하게 쿠키 설정 (보안 정책 통일)
        long sevenDaysInSeconds = 7 * 24 * 60 * 60;
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(sevenDaysInSeconds)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        TokenResponseDto tokenResponse = authService.login(loginDto);

        long sevenDaysInSeconds = 7 * 24 * 60 * 60;
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(sevenDaysInSeconds)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(tokenResponse);
    }

    @Operation(summary = "로그아웃", description = "DB에서 Refresh Token을 영구 삭제하고 브라우저의 쿠키를 무효화합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        authService.logout(userDetails.getId());

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @Operation(summary = "토큰 재발급", description = "쿠키에 저장된 Refresh Token을 이용해 새로운 Access Token을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TokenResponseDto tokenResponse = authService.reissue(refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "소셜 계정 본인인증 및 통합",
            description = "소셜 로그인 직후 발급받은 임시 토큰(Header)과 본인인증 ID(Body)를 받아 기존 계정과 통합하거나 정식 회원으로 승격시킵니다."
    )
    @PostMapping("/oauth2/integrate")
    public ResponseEntity<TokenResponseDto> integrateSocialAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Map<String, String> requestBody) {

        String verificationId = requestBody.get("verificationId");

        if (verificationId == null || verificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("본인인증 ID(verificationId)가 필요합니다.");
        }
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TokenResponseDto tokenResponse = authService.integrateSocialAccount(userDetails.getId(), verificationId);

        long sevenDaysInSeconds = 7 * 24 * 60 * 60;
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(sevenDaysInSeconds)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(tokenResponse);
    }
}