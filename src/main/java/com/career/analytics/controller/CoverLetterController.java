package com.career.analytics.controller;

import com.career.analytics.dto.CoverLetterRequestDto;
import com.career.analytics.dto.CoverLetterResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.CoverLetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cover-letters")
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> createCoverLetter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CoverLetterRequestDto requestDto) {
        return ResponseEntity.ok(coverLetterService.createCoverLetter(userDetails.getId(), requestDto));
    }

    /**
     * 특정 자기소개서 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> getCoverLetter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {

        return ResponseEntity.ok(coverLetterService.getCoverLetter(userDetails.getId(), id));
    }
    /**
     * 내 자기소개서 전체 목록 조회
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CoverLetterResponseDto>> getMyCoverLetters(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(coverLetterService.getMyCoverLetters(userDetails.getId()));
    }

    /**
     * 자기소개서 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> updateCoverLetter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CoverLetterRequestDto requestDto) {
        return ResponseEntity.ok(coverLetterService.updateCoverLetter(userDetails.getId(), id, requestDto));
    }

    /**
     * 자기소개서 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCoverLetter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        coverLetterService.deleteCoverLetter(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}