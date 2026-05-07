package com.career.analytics.controller;

import com.career.analytics.dto.AwardRequestDto;
import com.career.analytics.dto.AwardResponseDto;
import com.career.analytics.service.AwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Award API", description = "이력서 - 수상 정보 관리 API")
@RestController
// ✨ 1. URL 경로 변경: 이력서 하위 자원으로 구조화 완료!
@RequestMapping("/api/resumes/{resumeId}/awards")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AwardController {

    private final AwardService awardService;

    @Operation(summary = "특정 이력서의 수상 목록 조회", description = "해당 이력서에 등록된 모든 수상 내역을 최신 수상일 순으로 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AwardResponseDto>> getAwards(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId) { //  경로에서 resumeId 확보
        // ✨ 서비스 메서드명 변경 반영 (getAwardsByResume)
        return ResponseEntity.ok(awardService.getAwardsByResume(resumeId));
    }

    @Operation(summary = "수상 정보 추가", description = "특정 이력서에 새로운 수상 내역을 추가합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AwardResponseDto> createAward(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 경로에서 resumeId 확보
            @Valid @RequestBody AwardRequestDto dto) {
        // ✨ 서비스에 resumeId 전달
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(awardService.createAward(resumeId, dto));
    }

    @Operation(summary = "수상 정보 수정", description = "기존에 등록된 수상 내역을 수정합니다.")
    @PutMapping("/{awardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AwardResponseDto> updateAward(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성을 위한 유지
            @Parameter(description = "수정할 수상 내역의 고유 ID") @PathVariable("awardId") Long awardId,
            @Valid @RequestBody AwardRequestDto dto) {
        return ResponseEntity.ok(awardService.updateAward(awardId, dto));
    }

    @Operation(summary = "수상 정보 삭제", description = "등록된 수상 내역을 삭제합니다.")
    @DeleteMapping("/{awardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAward(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성을 위한 유지
            @Parameter(description = "삭제할 수상 내역의 고유 ID") @PathVariable("awardId") Long awardId) {
        awardService.deleteAward(awardId);
        return ResponseEntity.noContent().build();
    }
}