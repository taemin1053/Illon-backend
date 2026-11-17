package com.career.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class AiAnalysisResponseDto {

    @JsonProperty("overall_summary")
    private OverallSummaryDto overallSummary;

    private EvaluationItemsDto items;

    @JsonProperty("needs_improvement")
    private NeedsImprovementDto needsImprovement;

    @JsonProperty("recommended_activities")
    private List<RecommendedActivityDto> recommendedActivities;

    private String status;

    /**
     * 💡 추천 활동 API로부터 응답받은 데이터를 병합하는 메서드
     */
    public void mergeRecommendedActivities(List<RecommendedActivityDto> activities) {
        this.recommendedActivities = activities;
    }

    // --- Inner Static Classes ---

    @Getter
    @NoArgsConstructor
    public static class OverallSummaryDto {
        private String title;

        @JsonProperty("summary_text")
        private String summaryText;
    }

    @Getter
    @NoArgsConstructor
    public static class EvaluationItemsDto {
        @JsonProperty("job_fit")
        private EvaluationItemDetailDto jobFit;

        @JsonProperty("experience_specificity")
        private EvaluationItemDetailDto experienceSpecificity;

        @JsonProperty("technical_competency")
        private EvaluationItemDetailDto technicalCompetency;

        @JsonProperty("document_quality")
        private EvaluationItemDetailDto documentQuality;

        @JsonProperty("consistency_uniqueness")
        private EvaluationItemDetailDto consistencyUniqueness;
    }

    @Getter
    @NoArgsConstructor
    public static class EvaluationItemDetailDto {
        private String criterion;
        private String meaning;

        @JsonProperty("reference_basis")
        private String referenceBasis;

        private String status;
        private String level;
        private String reason;

        @JsonProperty("analysis_text")
        private String analysisText;
    }

    @Getter
    @NoArgsConstructor
    public static class NeedsImprovementDto {
        @JsonProperty("weak_keywords")
        private List<String> weakKeywords;

        private Map<String, Object> items; // 유연한 대응을 위해 Map 구조 채택
    }

    @Getter
    @NoArgsConstructor
    public static class RecommendedActivityDto {
        private String title;
        private String description;
        private String category;
    }

    /**
     * 💡 추천 활동 API(POST /api/v1/analyze/activities) 전용 매핑 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class RecommendedActivitiesResponse {
        @JsonProperty("recommended_activities")
        private List<RecommendedActivityDto> recommendedActivities;
    }
}
