package com.career.analytics.controller;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.dto.JobListResponseDto;
import com.career.analytics.dto.JobSummaryResponseDto;
import com.career.analytics.service.JobBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    //GET /api/jobs?location=천안시
    @Operation(
            summary = "특정 지역 공고 검색",
            description = "지역(location) 이름으로 공고를 검색하여 화면에 표시할 공고 리스트 객체를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<JobListResponseDto> searchJobsByLocation(
            @RequestParam("location") String location // 💡 여기에 ("location") 추가!
    ) {
        log.info("프론트엔드에서 지역 공고 검색 API를 호출했습니다. 지역: {}", location);

        JobListResponseDto response = jobBffService.searchJobsByLocation(location);

        return ResponseEntity.ok(response);
    }
}