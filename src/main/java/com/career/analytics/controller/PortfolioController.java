package com.career.analytics.controller;

import com.career.analytics.dto.PortfolioRequestDto;
import com.career.analytics.dto.PortfolioResponseDto;
import com.career.analytics.entity.Portfolio;
import com.career.analytics.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Tag(name = "Portfolio API", description = "이력서 - 포트폴리오(링크) API입니다.")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @Operation(summary = "내 포트폴리오 조회", description = "로그인한 유저의 포트폴리오를 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PortfolioResponseDto>> getMyPortfolio() {
        return ResponseEntity.ok(portfolioService.getMyPortfolios());
    }

    @Operation(summary = "내 포트폴리오 생성", description = "로그인한 유저의 포트폴리오를 생성합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDto> createPortfolio(@Valid @RequestBody PortfolioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(dto));
    }

    @Operation(summary = "내 포트폴리오 수정",description = "로그인한 유저의 포트폴리오를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDto> updatePortfolio(@Parameter(description = "수정할 포트폴리오 고유 ID")@PathVariable("id") Long id, @Valid @RequestBody PortfolioRequestDto dto) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, dto));
    }

    @Operation(summary = "내 포트폴리오 삭제",description = "로그인한 유저의 포트폴리오를 삭제합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePortfolio(@Parameter(description = "삭제할 포트폴리오 고유 ID")@PathVariable("id") Long id) {

        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

}
