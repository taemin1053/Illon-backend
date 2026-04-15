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

@Tag(name = "Certification", description = "이력서 - 자극증 관리 API")
@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * 현재 로그인한 사용자의 모든 자격증을 최신순으로 조회합니다.
     */
    @Operation(summary = "자격증 목록 조회", description = "로그인한 유저의 자격증 리스트를 취득일 내림차순으로 가져옵니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CertificationResponseDto>> getMyCertifications() {
        return ResponseEntity.ok(certificationService.getMyCertifications());
    }

    /**
     * 새로운 자격증 정보를 저장합니다.
     */
    @Operation(summary = "자격증 추가", description = "자격증명, 발급기관, 취득일 등의 정보를 입력하여 새 자격증을 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CertificationResponseDto> createCertification(
            @Valid @RequestBody CertificationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificationService.createCertification(dto));
    }

    /**
     * 기존에 등록된 자격증 정보를 수정합니다.
     * @param certificationId 수정할 대상의 PK
     */
    @Operation(summary = "자격증 수정", description = "ID에 해당하는 자격증 정보를 업데이트합니다. 본인 데이터만 수정 가능합니다.")
    @PutMapping("/{certificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CertificationResponseDto> updateCertification(
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationRequestDto dto) {
        return ResponseEntity.ok(certificationService.updateCertification(certificationId, dto));
    }

    /**
     * 등록된 자격증 정보를 삭제합니다.
     */
    @Operation(summary = "자격증 삭제", description = "ID에 해당하는 자격증 정보를 삭제합니다. 본인 데이터만 삭제 가능합니다.")
    @DeleteMapping("/{certificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certificationId) {
        certificationService.deleteCertification(certificationId);
        return ResponseEntity.noContent().build();
    }
}
