package com.career.analytics.controller;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // 생성자 주입
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;



     //회원 가입, 경로 : POST /api/auth/signup
    @PostMapping("/signup")
    // 2. 리턴 타입 명확화
    public ResponseEntity<TokenResponseDto> signup(@Valid @RequestBody SignupRequestDto signupDto) {
        TokenResponseDto tokenResponse = authService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }



    // 로그인 파트, 경로:  POST /api/auth/login
    @PostMapping("/login")
    // 2. 리턴 타입 명확화
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {

        TokenResponseDto tokenResponse = authService.login(loginDto);
        //Cookie 생성
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(14*24*60*60)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(tokenResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

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
