package com.career.analytics.dto;

/**
 * 대시보드용 오늘의 스크랩 현황 응답 DTO
 */
public record ScrapStatusResponseDto(
        long newJobsCount,       // 최근 3일 내 등록된 새 공고 수
        long expiringJobsCount,  // 마감 D-3 이내 임박 공고 수
        long totalScrapsCount    // 전체 스크랩 수
) {}
