package com.career.analytics.service;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.dto.JobListResponseDto;
import com.career.analytics.dto.JobSummaryResponseDto;
import com.career.analytics.dto.JobSearchCondition;
import com.career.analytics.dto.KakaoAddressResponseDto;
import com.career.analytics.dto.RecommendationResponseDto; // 💡 추천 DTO 임포트
import com.career.analytics.dto.ScrapedJobResponseDto; // 💡 스크랩 응답 DTO 임포트
import com.career.analytics.entity.ScrapedJob;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.JobNotFoundException;
import com.career.analytics.repository.ScrapJobRepository; // 💡 레포지토리 임포트
import com.career.analytics.repository.UserRepository;     // 💡 레포지토리 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobBffService {

    private final RestClient aiServerRestClient;
    private final KakaoAddressService kakaoAddressService;
    private final UserRepository userRepository;         // 💡 주입 추가
    private final ScrapJobRepository scrapJobRepository; // 💡 주입 추가

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

        // 2. 카카오 Local API를 통한 텍스트 주소 -> 위도, 경도 좌표 변환
        String lat = "37.5665"; // 기본값 (서울시청 위도)
        String lng = "126.9780"; // 기본값 (서울시청 경도)
        try {
            if (raw.location() != null && !raw.location().isBlank()) {
                KakaoAddressResponseDto.DocumentDto coordinate = kakaoAddressService.getCoordinate(raw.location());
                lat = coordinate.y(); // 위도
                lng = coordinate.x(); // 경도
            }
        } catch (Exception e) {
            log.warn("⚠️ 공고 주소 좌표 변환 실패. 기본 좌표를 사용합니다. 주소: {}, 에러: {}", raw.location(), e.getMessage());
        }

        // 3. 위도, 경도 정보가 포함된 DTO로 변환하여 반환
        return new JobDetailResponseDto(
                raw.id(), raw.title(), raw.company(), raw.location(),
                raw.job_type(), raw.occupation(), raw.career_type(), raw.education(),
                raw.salary(), raw.description(), raw.requirements(), raw.preferred(),
                raw.benefits(), raw.process(), raw.deadline(), raw.source(),
                raw.url(), raw.view_count(), raw.created_at(),
                lat, lng // 💡 위도, 경도 매핑 추가
        );
    }

    private record AiPaginatedResponse(
            int total,
            int page,
            int size,
            List<RawJobData> items
    ) {}

    /**
     * 공고 동적 검색 (검색 조건별 Caffeine 캐싱 적용)
     */
    @Cacheable(value = "jobSearchDynamic", key = "#condition.toCacheKey()")
    public JobListResponseDto searchJobs(JobSearchCondition condition) {
        log.info("🚀 [Cache Miss] AI 서버로 공고 동적 검색을 요청합니다. 조건: {}", condition);

        try {
            AiPaginatedResponse aiResponse = aiServerRestClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/api/v1/jobs");
                        
                        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
                            uriBuilder.queryParam("keyword", condition.getKeyword());
                        }
                        if (condition.getLocation() != null && !condition.getLocation().isBlank()) {
                            uriBuilder.queryParam("location", condition.getLocation());
                        }
                        if (condition.getJobCategory() != null && !condition.getJobCategory().isBlank()) {
                            uriBuilder.queryParam("job_type", condition.getJobCategory());
                        }
                        if (condition.getCareer() != null && !condition.getCareer().isBlank()) {
                            uriBuilder.queryParam("career_type", condition.getCareer());
                        }
                        if (condition.getEducation() != null && !condition.getEducation().isBlank()) {
                            uriBuilder.queryParam("education", condition.getEducation());
                        }
                        // 💡 DTO의 안전한 Getter를 호출하여 삼항 연산자 제거
                        uriBuilder.queryParam("page", condition.getPage());
                        uriBuilder.queryParam("size", condition.getSize());
                        
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        log.warn("AI 서버에 검색 조건에 맞는 공고가 존재하지 않습니다.");
                    })
                    .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                        log.error("AI 서버 내부 장애 발생");
                        throw new RuntimeException("AI 서버 응답 지연으로 공고를 불러올 수 없습니다.");
                    })
                    .body(AiPaginatedResponse.class);

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
            log.error("공고 동적 검색 중 치명적 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공고 검색에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * 💡 AI 서버의 원본 추천 응답을 받기 위한 내부 전용 DTO (Record)
     */
    private record RawAiRecommendation(
            String id,
            String user_id,
            String job_id,
            String created_at
    ) {}

    /**
     * 그리드용 설문 기반 맞춤 추천 공고 조회 (5개)
     * 호출 URI: GET /api/jobs/recommendations
     */
    public List<RecommendationResponseDto> getRecommendedJobsForGrid(Long userId) {
        return getRecommendedJobs(userId, 5);
    }

    /**
     * 캐러셀용 설문 기반 맞춤 추천 공고 조회 (12개)
     * 호출 URI: GET /api/jobs/recommendations/survey
     */
    public List<RecommendationResponseDto> getRecommendedJobsForCarousel(Long userId) {
        return getRecommendedJobs(userId, 12);
    }

    /**
     * 💡 AI 서버 추천 호출 및 DTO 변환 공통 헬퍼 메서드
     */
    private List<RecommendationResponseDto> getRecommendedJobs(Long userId, int topK) {
        log.info("🚀 [BFF] AI 서버로부터 맞춤 추천 공고 데이터를 조회합니다. 유저 ID: {}, top_k: {}", userId, topK);

        // 1. 로그인 유저 엔티티 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        // 2. N+1 문제 방지를 위해 유저가 스크랩한 공고의 ID들을 한 번에 메모리(Set)로 가져옴
        List<ScrapedJob> scrapedJobs = scrapJobRepository.findByUserOrderByCreatedAtDesc(user);
        Set<String> scrapedJobIds = scrapedJobs.stream()
                .map(ScrapedJob::getJobId)
                .collect(Collectors.toSet());

        // 3. AI 추천 서버 호출 (POST /api/v1/recommendations/{user_id}/general?top_k={topK})
        List<RawAiRecommendation> rawRecommendations;
        try {
            rawRecommendations = aiServerRestClient.post()
                    .uri("/api/v1/recommendations/{user_id}/general?top_k=" + topK, String.valueOf(userId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RawAiRecommendation>>() {});
        } catch (Exception e) {
            // 💡 예외 처리 방어막: 설문 미참여(422) 및 통신 장애 발생 시 500 에러를 내지 않고 조용히 빈 배열 반환
            log.warn("⚠️ AI 추천 서버 호출 실패 (설문 미참여 또는 통신 에러). 유저 ID: {}, 에러: {}", userId, e.getMessage());
            return List.of();
        }

        // 4. 방어 로직: AI 서버 응답이 없는 경우 빈 리스트 반환
        if (rawRecommendations == null || rawRecommendations.isEmpty()) {
            return List.of();
        }

        // 5. 추천된 job_id들을 순회하며 상세 정보를 조회하고 DTO 매핑
        List<RecommendationResponseDto> recommendedJobs = new ArrayList<>();
        for (RawAiRecommendation rec : rawRecommendations) {
            String jobId = rec.job_id();
            try {
                // 외부 AI 서버 상세 조회 API 호출
                RawJobDetail rawJob = aiServerRestClient.get()
                        .uri("/api/v1/jobs/{job_id}", jobId)
                        .retrieve()
                        .onStatus(status -> status.value() == 404, (request, response) -> {
                            log.warn("⚠️ AI 서버에 추천된 공고가 존재하지 않습니다. JobID: {}", jobId);
                        })
                        .body(RawJobDetail.class);

                if (rawJob != null) {
                    recommendedJobs.add(new RecommendationResponseDto(
                            rawJob.id(),
                            rawJob.title(),
                            rawJob.company(),
                            rawJob.location(),
                            rawJob.career_type(),
                            scrapedJobIds.contains(rawJob.id()) // 메모리 Set 기반 스크랩 여부 매핑
                    ));
                }
            } catch (Exception e) {
                // 개별 공고 상세조회 실패 시 전체 로직이 터지지 않도록 예외를 방어하고 다음 공고 진행
                log.error("⚠️ 추천 공고 상세 정보 조회 중 오류 발생. JobID: {}, Error: {}", jobId, e.getMessage());
            }
        }

        return recommendedJobs;
    }

    /**
     * 유저의 스크랩 공고 목록 페이징 조회 및 외부 API 상세 정보 매핑
     */
    public Page<ScrapedJobResponseDto> getScrapedJobs(Long userId, Pageable pageable) {
        // 1. 로그인 유저 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        // 2. 해당 유저가 스크랩한 내역 페이징 조회
        Page<ScrapedJob> scrapedJobsPage = scrapJobRepository.findByUser(user, pageable);

        // 3. Page 안의 ScrapedJob들을 순회하면서 외부 API 상세 정보(제목, 회사명 등)를 가져와 DTO로 변환
        return scrapedJobsPage.map(scrapedJob -> {
            String jobId = scrapedJob.getJobId();
            try {
                // 외부 AI 서버 상세 조회 API 호출
                RawJobDetail raw = aiServerRestClient.get()
                        .uri("/api/v1/jobs/{job_id}", jobId)
                        .retrieve()
                        .onStatus(status -> status.value() == 404, (request, response) -> {
                            log.warn("⚠️ AI 서버에 해당 공고가 존재하지 않습니다. JobID: {}", jobId);
                        })
                        .body(RawJobDetail.class);

                if (raw != null) {
                    return new ScrapedJobResponseDto(
                            raw.id(),
                            raw.title(),
                            raw.company(),
                            raw.location(),
                            raw.career_type()
                    );
                }
            } catch (Exception e) {
                log.error("⚠️ 외부 공고 API 호출 중 오류 발생. JobID: {}, Error: {}", jobId, e.getMessage());
            }

            // 외부 API 조회 실패 시 최소한의 식별 정보와 기본값을 담아 안전하게 반환
            return new ScrapedJobResponseDto(
                    jobId,
                    "정보를 불러올 수 없는 공고입니다.",
                    "알 수 없음",
                    "알 수 없음",
                    "알 수 없음"
            );
        });
    }
}