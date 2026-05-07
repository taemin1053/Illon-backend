package com.career.analytics.controller;

import com.career.analytics.dto.CertificationRequestDto;
import com.career.analytics.dto.CertificationResponseDto;
import com.career.analytics.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Certification", description = "이력서 - 자격증 관리 API")
@RestController
// ✨ 1. URL 경로 변경: 이력서(resumes)의 하위 자원으로 완벽하게 종속시킵니다.
@RequestMapping("/api/resumes/{resumeId}/certifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(summary = "특정 이력서의 자격증 목록 조회", description = "해당 이력서에 등록된 자격증 리스트를 취득일 내림차순으로 가져옵니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CertificationResponseDto>> getCertifications(
            @PathVariable("resumeId") Long resumeId) { // ✨ 경로에서 resumeId 확보
        // ✨ 서비스 메서드명 변경 반영
        return ResponseEntity.ok(certificationService.getCertificationsByResume(resumeId));
    }

    @Operation(summary = "자격증 추가", description = "특정 이력서에 새 자격증을 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CertificationResponseDto> createCertification(
            @PathVariable("resumeId") Long resumeId, // ✨ 경로에서 resumeId 확보
            @Valid @RequestBody CertificationRequestDto dto) {
        // ✨ 서비스에 resumeId 전달
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificationService.createCertification(resumeId, dto));
    }

    @Operation(summary = "자격증 수정", description = "ID에 해당하는 자격증 정보를 업데이트합니다.")
    @PutMapping("/{certificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CertificationResponseDto> updateCertification(
            @PathVariable("resumeId") Long resumeId, // 일관성을 위해 유지
            @PathVariable("certificationId") Long certificationId,
            @Valid @RequestBody CertificationRequestDto dto) {
        return ResponseEntity.ok(certificationService.updateCertification(certificationId, dto));
    }

    @Operation(summary = "자격증 삭제", description = "ID에 해당하는 자격증 정보를 삭제합니다.")
    @DeleteMapping("/{certificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCertification(
            @PathVariable("resumeId") Long resumeId, // 일관성을 위해 유지
            @PathVariable("certificationId") Long certificationId) {
        certificationService.deleteCertification(certificationId);
        return ResponseEntity.noContent().build();
    }
}