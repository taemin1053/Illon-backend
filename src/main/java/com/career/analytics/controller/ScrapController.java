package com.career.analytics.controller;

import com.career.analytics.dto.ScrapStatusResponseDto; // 💡 신규 DTO 임포트
import com.career.analytics.dto.ScrapedJobResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.JobBffService;
import com.career.analytics.service.ScrapedJobService; // 💡 신규 서비스 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Scrap Query API", description = "유저 스크랩 조회 API")
@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final JobBffService jobBffService;
    private final ScrapedJobService scrapedJobService; // 💡 현황 조회를 위해 서비스 주입 추가

    @Operation(
            summary = "내가 스크랩한 채용 공고 페이징 조회",
            description = "로그인한 유저가 스크랩한 채용 공고 목록을 페이징 처리하여 최신순으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<Page<ScrapedJobResponseDto>> getScrapedJobs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("📢 유저가 스크랩한 공고 목록 조회를 요청했습니다. 유저 ID: {}, 페이징: {}", userDetails.getId(), pageable);
        
        Page<ScrapedJobResponseDto> response = jobBffService.getScrapedJobs(userDetails.getId(), pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 💡 [GET] 오늘의 스크랩 현황 조회
     * 호출 URI: GET /api/scraps/status
     */
    @Operation(
            summary = "오늘의 스크랩 현황 조회",
            description = "로그인한 유저의 전체 스크랩 개수, 최근 3일 내 등록된 새 공고 개수, 마감 D-3 이내인 임박 공고 개수를 반환합니다."
    )
    @GetMapping("/status")
    public ResponseEntity<ScrapStatusResponseDto> getScrapStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("📢 유저의 오늘의 스크랩 현황 조회를 요청했습니다. 유저 ID: {}", userDetails.getId());

        ScrapStatusResponseDto response = scrapedJobService.getScrapStatus(userDetails.getId());

        return ResponseEntity.ok(response);
    }
}
