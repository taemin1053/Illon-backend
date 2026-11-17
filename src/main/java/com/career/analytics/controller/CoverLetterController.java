package com.career.analytics.controller;

import com.career.analytics.dto.CoverLetterRequestDto;
import com.career.analytics.dto.CoverLetterResponseDto;
import com.career.analytics.service.CoverLetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cover Letter API", description = "이력서 - 자기소개서 API")
@RestController
// ✨ 1. URL 경로를 이력서(resumes) 하위 자원으로 완벽하게 구조화 완료!
@RequestMapping("/api/resumes/{resumeId}/cover-letters")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @Operation(summary = "자기소개서 작성", description = "특정 이력서에 자기소개서를 작성합니다. (이력서당 1개만 가능)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> createCoverLetter(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId,
            @Valid @RequestBody CoverLetterRequestDto requestDto) {
        // ✨ 불필요한 UserDetails 파라미터를 걷어내고 서비스에 resumeId만 넘겨줍니다!
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coverLetterService.createCoverLetter(resumeId, requestDto));
    }

    @Operation(summary = "자기소개서 조회", description = "특정 이력서의 자기소개서를 단건 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> getCoverLetter(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId) {
        // ✨ 1:1 관계의 핵심: 컬렉션(List)이 아닌 단일 객체 반환
        return ResponseEntity.ok(coverLetterService.getCoverLetterByResume(resumeId));
    }

    @Operation(summary = "자기소개서 수정", description = "ID에 해당하는 자기소개서를 수정합니다.")
    @PutMapping("/{coverLetterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoverLetterResponseDto> updateCoverLetter(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성 유지
            @Parameter(description = "자기소개서 고유 ID") @PathVariable("coverLetterId") Long coverLetterId,
            @Valid @RequestBody CoverLetterRequestDto requestDto) {
        return ResponseEntity.ok(coverLetterService.updateCoverLetter(coverLetterId, requestDto));
    }

    @Operation(summary = "자기소개서 삭제", description = "ID에 해당하는 자기소개서를 삭제합니다.")
    @DeleteMapping("/{coverLetterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCoverLetter(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성 유지
            @Parameter(description = "자기소개서 고유 ID") @PathVariable("coverLetterId") Long coverLetterId) {
        coverLetterService.deleteCoverLetter(coverLetterId);
        return ResponseEntity.noContent().build();
    }
}