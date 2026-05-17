package com.career.analytics.controller;

import com.career.analytics.dto.*;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    /*
    @Operation(
            summary = "간편 회원가입 및 로그인",
            description = "포트원 본인인증 ID만으로 가입 또는 로그인을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(schema = @Schema(implementation = TokenResponseDto.class)))
            }
    )

    @PostMapping("/signup/simple")
    public ResponseEntity<TokenResponseDto> simpleSignup(
            @Valid @RequestBody SimpleSignupRequestDto request // 🔥 Map 대신 DTO 적용
    ) {
        TokenResponseDto tokenResponse = authService.simpleSignupAndLogin(request.verificationId());

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
    */
    @Operation(summary = "이메일 회원가입", description = "본인인증 ID와 이메일, 비밀번호를 받아 회원가입을 진행합니다.")
    @PostMapping("/signup/email")
    public ResponseEntity<SignupResponseDto> emailSignup(@Valid @RequestBody SignupRequestDto signupDto) {
        SignupResponseDto response = authService.emailSignup(signupDto, signupDto.getVerificationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        TokenResponseDto tokenResponse = authService.login(loginDto);

        //토큰 7일
        long sevenDaysInSeconds = 7 * 24 * 60 * 60;

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // https 환경에서만 전송
                .path("/api/auth/refresh") // 토큰 재발급 API에서만 쿠키가 전송되도록 보안 강화
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

    // 신규 추가: 소셜 로그인 후 본인인증(CI) 계정 통합 API
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

        // 서비스 로직 호출 및 새로운 통합 토큰 반환
        TokenResponseDto tokenResponse = authService.integrateSocialAccount(userDetails.getId(), verificationId);

        // 보안 정책 일관성 유지: 여기서도 리프레시 토큰을 HttpOnly 쿠키로 세팅해서 내려줍니다.
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