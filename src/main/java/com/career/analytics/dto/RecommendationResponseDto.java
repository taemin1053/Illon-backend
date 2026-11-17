package com.career.analytics.dto;

/**
 * 설문 기반 맞춤 추천 공고 응답 DTO
 * 
 * @param jobId      공고 고유 식별자
 * @param title      공고 제목
 * @param company    회사명
 * @param location   근무지 위치
 * @param careerType 경력 요구사항 (신입/경력 등)
 * @param isScraped  현재 유저의 스크랩(북마크) 여부
 */
public record RecommendationResponseDto(
        String jobId,
        String title,
        String company,
        String location,
        String careerType,
        boolean isScraped
) {
}
