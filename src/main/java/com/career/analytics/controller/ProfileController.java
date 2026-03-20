package com.career.analytics.controller;

import com.career.analytics.dto.ProfileUpdateRequestDto;
import com.career.analytics.dto.ProfileResponseDto;
import com.career.analytics.dto.UserFullProfileResponseDto;
import com.career.analytics.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Profile", description = "사용자 프로필 관리 API")
public class ProfileController {


    private final ProfileService profileService;


     // 내 프로필 조회 (Profile만)
     //GET /api/profile/me

    @Operation(summary = "내 프로필 단일 조회", description = "로그인한 사용자의 프로필 정보만 단독으로 조회합니다.")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")

    public ResponseEntity<ProfileResponseDto> getMyProfile() {

        ProfileResponseDto profileResponse = profileService.getMyProfile();
        return ResponseEntity.ok(profileResponse);
    }


     //내 전체 정보 조회 (Users + Profile)
     //GET /api/profile/me/full

    @Operation(summary = "내 전체 정보 조회", description = "로그인한 사용자의 기본 계정 정보(Users)와 프로필 정보(Profile)를 함께 조회합니다.")
    @GetMapping("/me/full")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserFullProfileResponseDto> getMyFullProfile() {
        UserFullProfileResponseDto fullProfile = profileService.getMyFullProfile();
        return ResponseEntity.ok(fullProfile);
    }


     //프로필 수정
     //PUT /api/profile

    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 이름(name), 성별(gender), 생년월일(birthDate), 자기소개(bio) 정보를 수정합니다.")
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponseDto> updateProfile(@Valid @RequestBody ProfileUpdateRequestDto profileDto) {
        ProfileResponseDto profileResponse = profileService.updateProfile(profileDto);
        return ResponseEntity.ok(profileResponse);
    }
}