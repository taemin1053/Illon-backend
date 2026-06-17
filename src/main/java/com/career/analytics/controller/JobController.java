package com.career.analytics.controller;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.dto.JobListResponseDto;
import com.career.analytics.dto.JobSearchCondition;
import com.career.analytics.dto.JobSummaryResponseDto;
import com.career.analytics.dto.RecommendationResponseDto; // 💡 임포트 추가
import com.career.analytics.security.UserDetailsImpl;       // 💡 임포트 추가
import com.career.analytics.service.JobBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 💡 임포트 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Job API", description = "채용 공고 데이터 중계(BFF) API")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobBffService jobBffService;

    //[GET] 메인 홈 전체 공고 요약 리스트 (30개)
    //프론트엔드 호출 URI: GET /api/jobs/main

    @Operation(
            summary = "메인 홈 공고 요약 30개 조회 (캐싱 적용)",
            description = "AI 서버에서 전체 공고를 가져와 프론트엔드 UI에 맞춰 키워드를 추출하고 30개만 반환합니다. (Caffeine 로컬 캐시 적용으로 초고속 응답)"
    )
    @GetMapping("/keywords")
    public ResponseEntity<List<JobSummaryResponseDto>> getMainJobSummaries() {
        log.info("프론트엔드에서 메인 홈 공고 리스트 API를 호출했습니다.");

        List<JobSummaryResponseDto> responses = jobBffService.getJobSummaries();

        return ResponseEntity.ok(responses);
    }

    /**
     * [GET] 특정 공고 상세 조회
     * 프론트엔드 호출 URI: GET /api/jobs/{jobId}
     */
    @Operation(
            summary = "특정 공고 상세 정보 조회",
            description = "AI 서버에서 특정 공고의 전체 상세 정보를 실시간으로 가져옵니다. (호출 시 조회수가 증가합니다)"
    )
    @GetMapping("/{jobId}")
    public ResponseEntity<JobDetailResponseDto> getJobDetail(@PathVariable("jobId") String jobId) {
        log.info("프론트엔드에서 공고 상세 조회 API를 호출했습니다. ID: {}", jobId);

        JobDetailResponseDto response = jobBffService.getJobDetail(jobId);

        return ResponseEntity.ok(response);
    }

    /**
     * [GET] 공고 동적 검색 및 필터링 (BFF 라우팅)
     * 호출 URI: GET /api/jobs
     */
    @Operation(
            summary = "공고 동적 검색 및 필터링 (캐싱 적용)",
            description = "검색 조건(키워드, 지역, 경력, 직군, 학력 등)을 기반으로 AI 서버의 공고 데이터를 동적으로 검색하여 반환합니다."
    )
    @GetMapping
    public ResponseEntity<JobListResponseDto> searchJobs(
            @ModelAttribute JobSearchCondition condition
    ) {
        log.info("프론트엔드에서 공고 검색 API를 호출했습니다. 조건: {}", condition);

        JobListResponseDto response = jobBffService.searchJobs(condition);

        return ResponseEntity.ok(response);
    }

    /**
     * [GET] 설문 기반 맞춤 추천 공고 조회 (5개 제한 및 스크랩 정보 포함) - 그리드 화면용
     * 호출 URI: GET /api/jobs/recommendations
     */
    @Operation(
            summary = "설문 기반 맞춤 추천 공고 5개 조회 ",
            description = "인증된 사용자의 ID를 기반으로 AI 서버에서 맞춤 공고를 호출한 후 로컬 DB 스크랩 여부를 매핑하여 상위 5개를 반환합니다."
    )
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationResponseDto>> getRecommendedJobsForGrid(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 💡 안전장치: 인증 정보가 없는 경우 401 에러를 명시적으로 반환
        if (userDetails == null) {
            log.warn("인증되지 않은 사용자가 추천 공고 API를 호출했습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("프론트엔드에서 그리드용 맞춤 추천 공고 API를 호출했습니다. 유저 ID: {}", userDetails.getId());
        List<RecommendationResponseDto> responses = jobBffService.getRecommendedJobsForGrid(userDetails.getId());
        return ResponseEntity.ok(responses);
    }
    /**
     * [GET] 설문 기반 맞춤 추천 공고 조회 (12개 제한 및 스크랩 정보 포함) - 캐러셀 화면용
     * 호출 URI: GET /api/jobs/recommendations/survey
     */
    @Operation(
            summary = "설문 기반 맞춤 추천 공고 12개 조회 ",
            description = "인증된 사용자의 ID를 기반으로 AI 서버에서 맞춤 공고를 호출한 후 로컬 DB 스크랩 여부를 매핑하여 상위 12개를 반환합니다."
    )
    @GetMapping("/recommendations/survey")
    public ResponseEntity<List<RecommendationResponseDto>> getRecommendedJobsForCarousel(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("프론트엔드에서 캐러셀용 맞춤 추천 공고 API를 호출했습니다. 유저 ID: {}", userDetails.getId());

        List<RecommendationResponseDto> responses = jobBffService.getRecommendedJobsForCarousel(userDetails.getId());

        return ResponseEntity.ok(responses);
    }
}