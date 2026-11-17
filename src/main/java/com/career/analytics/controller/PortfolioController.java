package com.career.analytics.controller;

import com.career.analytics.dto.PortfolioRequestDto;
import com.career.analytics.dto.PortfolioResponseDto;
import com.career.analytics.service.PortfolioService;
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

@RestController
// ✨ 1. 대망의 URL 변경: 이력서(resumes) 하위 자원으로 완벽하게 구조화 완료!
@RequestMapping("/api/resumes/{resumeId}/portfolios")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Tag(name = "Portfolio API", description = "이력서 - 포트폴리오(링크) API입니다.")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "특정 이력서의 포트폴리오 목록 조회", description = "해당 이력서에 등록된 모든 포트폴리오(링크)를 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PortfolioResponseDto>> getPortfolios(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId) { // ✨ 경로에서 resumeId 확보
        // ✨ 서비스 메서드명 변경 완벽 대응
        return ResponseEntity.ok(portfolioService.getPortfoliosByResume(resumeId));
    }

    @Operation(summary = "포트폴리오 추가", description = "특정 이력서에 새로운 포트폴리오(링크)를 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDto> createPortfolio(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // ✨ 경로에서 resumeId 확보
            @Valid @RequestBody PortfolioRequestDto dto) {
        // ✨ 서비스에 resumeId 전달
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(resumeId, dto));
    }

    @Operation(summary = "포트폴리오 수정",description = "ID에 해당하는 포트폴리오를 수정합니다.")
    @PutMapping("/{portfolioId}") // ✨ id를 portfolioId로 명확하게 변경
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDto> updatePortfolio(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성 유지
            @Parameter(description = "수정할 포트폴리오 고유 ID") @PathVariable("portfolioId") Long portfolioId,
            @Valid @RequestBody PortfolioRequestDto dto) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(portfolioId, dto));
    }

    @Operation(summary = "포트폴리오 삭제",description = "ID에 해당하는 포트폴리오를 삭제합니다.")
    @DeleteMapping("/{portfolioId}") // ✨ id를 portfolioId로 명확하게 변경
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePortfolio(
            @Parameter(description = "이력서 고유 ID") @PathVariable("resumeId") Long resumeId, // 일관성 유지
            @Parameter(description = "삭제할 포트폴리오 고유 ID") @PathVariable("portfolioId") Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }
}