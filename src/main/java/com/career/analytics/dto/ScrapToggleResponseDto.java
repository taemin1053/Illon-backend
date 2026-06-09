package com.career.analytics.dto;

/**
 * 스크랩 토글 응답 DTO
 * 
 * @param status  스크랩 상태값 ("SCRAP_CREATED" 또는 "SCRAP_DELETED")
 * @param message 상세 결과 메시지
 */
public record ScrapToggleResponseDto(
        String status,
        String message
) {
}
