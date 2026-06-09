package com.career.analytics.controller;

import com.career.analytics.dto.ScrapToggleResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.ScrapedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class ScrapJobController {

    private final ScrapedJobService scrapedJobService;

    /**
     * 채용 공고 스크랩 토글 API
     * POST /api/jobs/{jobId}/scrap
     * 
     * @param userDetails Spring Security 인증 성공 후 주입된 UserDetailsImpl 객체
     * @param jobId       경로 파라미터로 받은 공고 식별자
     */
    @PostMapping("/{jobId}/scrap")
    public ResponseEntity<ScrapToggleResponseDto> toggleScrap(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("jobId") String jobId
    ) {
        String resultStatus = scrapedJobService.toggleScrap(userDetails.getId(), jobId);

        String message = "SCRAP_CREATED".equals(resultStatus)
                ? "채용 공고를 성공적으로 스크랩했습니다."
                : "채용 공고 스크랩을 취소했습니다.";

        return ResponseEntity.ok(new ScrapToggleResponseDto(resultStatus, message));
    }
}
