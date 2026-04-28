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

@Tag(name = "Language",description = "이력서 - 어학 정보 관리 API")
@RestController
@RequestMapping("/api/languages")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    @Operation(summary = "내 어학 목록 조회", description = "로그인된 사용자의 모든 어학 정보를 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LanguageResponseDto>> getMyLanguages() {
        return ResponseEntity.ok(languageService.getMyLanguages());
    }

    @Operation(summary = "어학 정보 추가", description = "새로운 어학 성적을 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LanguageResponseDto>createLanguage(@Valid @RequestBody LanguageRequestDto Dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(languageService.createLanguage(Dto));
    }

    @Operation(summary = "어학 정보 수정", description = "ID에 해당하는 어학 정보를 업데이트합니다. 본인 데이터만 수정 가능합니다.")
    @PutMapping("/{languageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LanguageResponseDto> updateLanguage(
            @PathVariable("languageId") Long languageId,
            @Valid @RequestBody LanguageRequestDto dto) {
        return ResponseEntity.ok(languageService.updateLanguage(languageId, dto));
    }
    @Operation(summary = "어학 정보 삭제", description = "ID에 해당하는 어학 정보를 삭제합니다. 본인 데이터만 삭제 가능합니다.")
    @DeleteMapping("/{languageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteLanguage(@PathVariable("languageId") Long languageId) {
        languageService.deleteLanguage(languageId);
        return ResponseEntity.noContent().build();
    }
}
