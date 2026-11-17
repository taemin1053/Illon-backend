package com.career.analytics.controller;

import com.career.analytics.dto.LanguageRequestDto;
import com.career.analytics.dto.LanguageResponseDto;
import com.career.analytics.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Language", description = "이력서 - 어학 정보 관리 API")
@RestController
// ✨ 1. URL 경로를 이력서(resumes) 하위 자원으로 구조화합니다.
@RequestMapping("/api/resumes/{resumeId}/languages")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @Operation(summary = "특정 이력서의 어학 목록 조회", description = "해당 이력서에 등록된 모든 어학 정보를 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LanguageResponseDto>> getLanguages(
            @PathVariable("resumeId") Long resumeId) { // ✨ 경로에서 resumeId 확보
        // ✨ 서비스 메서드명 변경 반영 (getLanguagesByResume)
        return ResponseEntity.ok(languageService.getLanguagesByResume(resumeId));
    }

    @Operation(summary = "어학 정보 추가", description = "특정 이력서에 새로운 어학 성적을 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LanguageResponseDto> createLanguage(
            @PathVariable("resumeId") Long resumeId, // ✨ 경로에서 resumeId 확보
            @Valid @RequestBody LanguageRequestDto dto) {
        // ✨ 서비스에 resumeId와 dto를 함께 전달
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(languageService.createLanguage(resumeId, dto));
    }

    @Operation(summary = "어학 정보 수정", description = "ID에 해당하는 어학 정보를 업데이트합니다.")
    @PutMapping("/{languageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LanguageResponseDto> updateLanguage(
            @PathVariable("resumeId") Long resumeId, // 일관성을 위해 유지
            @PathVariable("languageId") Long languageId,
            @Valid @RequestBody LanguageRequestDto dto) {
        return ResponseEntity.ok(languageService.updateLanguage(languageId, dto));
    }

    @Operation(summary = "어학 정보 삭제", description = "ID에 해당하는 어학 정보를 삭제합니다.")
    @DeleteMapping("/{languageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteLanguage(
            @PathVariable("resumeId") Long resumeId,
            @PathVariable("languageId") Long languageId) {
        languageService.deleteLanguage(languageId);
        return ResponseEntity.noContent().build();
    }
}