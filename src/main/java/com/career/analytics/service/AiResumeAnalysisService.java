package com.career.analytics.service;

import com.career.analytics.dto.AiAnalysisResponseDto;
import com.career.analytics.dto.AiAnalysisResponseDto.RecommendedActivityDto;
import com.career.analytics.dto.AiAnalysisResponseDto.RecommendedActivitiesResponse;
import com.career.analytics.exception.AiAnalysisRequestException;
import com.career.analytics.exception.AiServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiResumeAnalysisService {

    private final RestClient aiServerRestClient;
    
    @Qualifier("aiAnalysisExecutor")
    private final Executor aiAnalysisExecutor;

    /**
     * 💡 두 API(피드백 & 추천 활동)를 CompletableFuture를 통해 비동기 병렬로 동시에 호출하고 결과를 결합합니다.
     */
    public AiAnalysisResponseDto analyzeResume(String resumeText) {
        log.info("🚀 AI 서버 비동기 병렬 분석 시작 (길이: {})", 
                (resumeText != null) ? resumeText.length() : 0);

        if (resumeText == null || resumeText.isBlank()) {
            throw new AiAnalysisRequestException("분석할 이력서 텍스트가 비어있습니다.");
        }

        // 1. 피드백 API 호출 정의 (비동기)
        CompletableFuture<AiAnalysisResponseDto> feedbackFuture = CompletableFuture.supplyAsync(
                () -> fetchFeedback(resumeText), aiAnalysisExecutor
        );

        // 2. 추천 활동 API 호출 정의 (비동기)
        CompletableFuture<List<RecommendedActivityDto>> activitiesFuture = CompletableFuture.supplyAsync(
                () -> fetchRecommendedActivities(resumeText), aiAnalysisExecutor
        );

        // 3. 두 API 응답이 완료되면 논블로킹 상태로 결합 수행 후 반환
        try {
            return feedbackFuture.thenCombine(activitiesFuture, (feedback, activities) -> {
                log.info("📢 두 AI API 호출 응답 완료, 결과 결합을 수행합니다.");
                feedback.mergeRecommendedActivities(activities);
                return feedback;
            }).join(); // 조인 시점에 결과를 최종 대기함 (RestClient의 30초 타임아웃 설정을 따름)

        } catch (Exception e) {
            log.error("❌ AI 비동기 병렬 분석 처리 중 치명적 예외 발생: {}", e.getMessage(), e);
            // 💡 [수정] 비동기 예외 래핑을 풀어 실제 원인 커스텀 예외를 낚아채 전파합니다.
            Throwable cause = e.getCause();
            if (cause instanceof AiAnalysisRequestException) {
                throw (AiAnalysisRequestException) cause;
            }
            if (cause instanceof AiServerException) {
                throw (AiServerException) cause;
            }
            throw new AiServerException("AI 분석 중 통신 실패 또는 결과 병합 과정에서 에러가 발생했습니다.", e);
        }
    }

    /**
     * 💡 1번 API: 피드백 정보 원격 호출
     */
    private AiAnalysisResponseDto fetchFeedback(String resumeText) {
        log.info("🌐 [Async] 피드백 API 호출을 준비합니다. (URI: /resume/analyze/feedback)");
        try {
            return aiServerRestClient.post()
                    .uri("/resume/analyze/feedback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("resume_text", resumeText))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                        log.error("❌ [Feedback API 4xx] 요청 포맷 에러 - 상태코드: {}", response.getStatusCode());
                        // 💡 [수정] 커스텀 예외 적용
                        throw new AiAnalysisRequestException("AI 피드백 요청 형식이 올바르지 않습니다.");
                    })
                    .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                        log.error("❌ [Feedback API 5xx] AI 서버 장애 - 상태코드: {}", response.getStatusCode());
                        // 💡 [수정] 커스텀 예외 적용
                        throw new AiServerException("AI 피드백 서버 장애가 발생했습니다.");
                    })
                    .body(AiAnalysisResponseDto.class);
        } catch (Exception e) {
            log.error("❌ [Feedback API] 통신 장애 발생. 에러: {}", e.getMessage());
            throw e; // 상위 CompletableFuture가 에러를 수집할 수 있도록 전파
        }
    }

    /**
     * 💡 2번 API: 추천 활동 정보 원격 호출
     */
    private List<RecommendedActivityDto> fetchRecommendedActivities(String resumeText) {
        log.info("🌐 [Async] 추천 활동 API 호출을 준비합니다. (URI: /resume/analyze/activities)");
        try {
            RecommendedActivitiesResponse response = aiServerRestClient.post()
                    .uri("/resume/analyze/activities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("resume_text", resumeText))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (request, res) -> {
                        log.error("❌ [Activities API 4xx] 요청 포맷 에러 - 상태코드: {}", res.getStatusCode());
                        // 💡 [수정] 커스텀 예외 적용
                        throw new AiAnalysisRequestException("AI 추천 활동 요청 형식이 올바르지 않습니다.");
                    })
                    .onStatus(status -> status.is5xxServerError(), (request, res) -> {
                        log.error("❌ [Activities API 5xx] AI 서버 장애 - 상태코드: {}", res.getStatusCode());
                        // 💡 [수정] 커스텀 예외 적용
                        throw new AiServerException("AI 추천 활동 서버 장애가 발생했습니다.");
                    })
                    .body(RecommendedActivitiesResponse.class);

            return (response != null) ? response.getRecommendedActivities() : List.of();
        } catch (Exception e) {
            log.error("❌ [Activities API] 통신 장애 발생. 에러: {}", e.getMessage());
            throw e; // 상위 CompletableFuture가 에러를 수집할 수 있도록 전파
        }
    }
}
