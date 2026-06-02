package com.career.analytics.service;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.dto.JobListResponseDto;
import com.career.analytics.dto.JobSummaryResponseDto;
import com.career.analytics.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobBffService {

    private final RestClient aiServerRestClient;

    /**
     * 💡 AI 서버의 원본 응답을 받기 위한 내부 전용 DTO (Record)
     * API 문서 스펙에 맞춰 언더바(_) 네이밍을 그대로 수용합니다.
     * 이 클래스는 서비스 내부에서만 쓰고 외부(프론트)로는 절대 나가지 않습니다.
     */
    private record RawJobData(
            String id,
            String title,
            String company,
            String location,
            String job_type,
            String career_type,
            String education,
            String salary,
            String deadline,
            int view_count,
            String created_at
    ) {}

    /**
     * 메인 홈 공고 목록 조회 (Caffeine 캐싱 적용)
     * AI 서버의 무거운 전체 데이터를 가져와 UI에 필요한 30개 요약본으로 가공합니다.
     */
    @Cacheable(value = "jobSummaries", key = "'main_top_30'")
    public List<JobSummaryResponseDto> getJobSummaries() {
        log.info("🚀 [Cache Miss] AI 서버로부터 전체 공고 데이터를 실시간으로 호출합니다.");

        // 1. AI 서버에서 전체 데이터 받아오기
        List<RawJobData> rawDataList = aiServerRestClient.get()
                .uri("/api/v1/jobs/all")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RawJobData>>() {});

        // 방어 로직: AI 서버 응답이 비어있을 경우 안전하게 빈 리스트 반환
        if (rawDataList == null || rawDataList.isEmpty()) {
            return List.of();
        }

        // 2. 스트림을 활용한 데이터 다이어트 및 DTO 변환 (BFF의 핵심)
        return rawDataList.stream()
                .limit(30) // 메인 홈에는 딱 30개만 노출
                .map(job -> new JobSummaryResponseDto(
                        job.id(),
                        job.title(),
                        job.company(),
                        job.location(),
                        extractKeywords(job) // 💡 텍스트 데이터를 프론트엔드용 키워드 배열로 변환
                ))
                .toList(); // Java 16+ 불변 리스트 반환
    }

    /**
     * 원본 데이터를 분석하여 피그마(UI) 카드에 띄울 직관적인 키워드 배열을 생성합니다.
     */
    private List<String> extractKeywords(RawJobData job) {
        List<String> keywords = new ArrayList<>();

        // 데이터가 null이 아닐 경우에만 키워드로 추가
        if (job.job_type() != null && !job.job_type().isBlank()) {
            keywords.add(job.job_type());
        }
        if (job.career_type() != null && !job.career_type().isBlank()) {
            keywords.add(job.career_type());
        }
        if (job.education() != null && !job.education().isBlank()) {
            keywords.add(job.education());
        }

        return keywords;
    }

    /**
     * 💡 AI 서버의 원본 상세 데이터를 받기 위한 내부 전용 DTO
     */
    private record RawJobDetail(
            String id, String title, String company, String location, String job_type,
            String occupation, String career_type, String education, String salary,
            String description, String requirements, String preferred, String benefits,
            String process, String deadline, String source, String url, int view_count, String created_at
    ) {}

    /**
     * 특정 공고 상세 조회 (캐싱 미적용 - 조회수 실시간 반영)
     */
    public JobDetailResponseDto getJobDetail(String jobId) {
        log.info(" [Real-time] AI 서버에서 공고 상세 데이터를 실시간 조회합니다. ID: {}", jobId);

        // 1. AI 서버에서 언더바 필드를 가진 Raw 객체로 먼저 받기
        RawJobDetail raw = aiServerRestClient.get()
                .uri("/api/v1/jobs/{job_id}", jobId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new JobNotFoundException(jobId);
                })
                .body(RawJobDetail.class);

        if (raw == null) throw new JobNotFoundException(jobId);

        // 2. 여기서 수동으로 카멜케이스 DTO로 변환하여 반환
        return new JobDetailResponseDto(
                raw.id(), raw.title(), raw.company(), raw.location(),
                raw.job_type(), raw.occupation(), raw.career_type(), raw.education(),
                raw.salary(), raw.description(), raw.requirements(), raw.preferred(),
                raw.benefits(), raw.process(), raw.deadline(), raw.source(),
                raw.url(), raw.view_count(), raw.created_at()
        );
    }

    private record AiPaginatedResponse(
            int total,
            int page,
            int size,
            List<RawJobData> items
    ) {}

    /**
     * 특정 지역 공고 검색 (지역 파라미터 기반 캐싱 적용)
     */
    @Cacheable(value = "jobSearch", key = "#p0") // 💡 #p0는 파라미터의 첫 번째 인자(String location)를 명시적으로 가리킵니다.
    public JobListResponseDto searchJobsByLocation(String location) {
        log.info("🚀 [Real-time 또는 Cache-Miss] 지역별 공고 검색 API 실제 호출: location={}", location);
        try {
            AiPaginatedResponse aiResponse = aiServerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/jobs")
                            .queryParam("location", location)
                            .queryParam("size", 30)
                            .build())
                    .retrieve()
                    // 💡 1. 404 예외 방어 (해당 지역에 공고가 아예 없는 경우)
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        log.warn("AI 서버에 해당 지역({})의 공고가 없습니다.", location);
                    })
                    // 💡 2. 500 예외 방어 (AI 서버 자체가 뻗은 경우)
                    .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                        log.error("AI 서버 내부 장애 발생");
                        throw new RuntimeException("AI 서버 응답 지연으로 공고를 불러올 수 없습니다.");
                    })
                    .body(AiPaginatedResponse.class);

            // 데이터가 아예 없거나 404로 인해 aiResponse가 정상 파싱되지 않은 경우 빈 배열 반환
            if (aiResponse == null || aiResponse.items() == null || aiResponse.items().isEmpty()) {
                log.info("반환할 공고 데이터가 없어 빈 리스트를 반환합니다.");
                return new JobListResponseDto(List.of());
            }

            List<JobSummaryResponseDto> processedJobs = aiResponse.items().stream()
                    .map(job -> new JobSummaryResponseDto(
                            job.id(),
                            job.title(),
                            job.company(),
                            job.location(),
                            extractKeywords(job)
                    ))
                    .toList();

            return new JobListResponseDto(processedJobs);

        } catch (Exception e) {
            // 💡 3. 타임아웃 등 예측 불가능한 통신 에러 최후 방어선
            log.error("지역별 공고 검색 중 치명적 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공고 검색에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
}