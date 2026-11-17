package com.career.analytics.dto;

import java.util.List;

// BFF 레이어의 DTO: 프론트엔드가 요구하는 데이터만 골라 담음
public record JobSummaryResponseDto(
        String id,
        String title,
        String company,
        String location,
        List<String> keywords // AI 서버에서 분석된 키워드 배열
) {}
