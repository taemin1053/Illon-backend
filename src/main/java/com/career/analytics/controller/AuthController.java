package com.career.analytics.controller;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.SignupResponseDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증(Auth) API", description = "회원가입, 로그인, 로그아웃 등 보안 및 토큰 관리 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponse(
            responseCode = "400",
            description = "실패",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"잘못된 요청입니다.\"}"))
    )
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupDto) {

        SignupResponseDto response = authService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증하고 Access Token과 Refresh Token(HttpOnly 쿠키)을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
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

        // 1. 토큰이 이상하면 401
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 서비스 호출해서 백엔드 DB 토큰 삭제 (정상 유저일 때만)
        authService.logout(userDetails.getId());

        // 3. 프론트엔드 쿠키 수명 0으로 만들어서 삭제 명령
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
}