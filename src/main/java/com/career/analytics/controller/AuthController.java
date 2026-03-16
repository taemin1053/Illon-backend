package com.career.analytics.controller;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(tokenResponse);
    }
}
