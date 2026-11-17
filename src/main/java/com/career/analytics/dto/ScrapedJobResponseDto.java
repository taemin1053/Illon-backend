package com.career.analytics.dto;

/**
 * 마이페이지 내 스크랩 공고 카드용 DTO (Java 14+ Record)
 */
public record ScrapedJobResponseDto(
        String jobId,
        String title,
        String company,
        String location,
        String careerType
) {}
