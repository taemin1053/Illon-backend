package com.career.analytics.dto;

import com.career.analytics.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioResponseDto {
    private Long id;
    private Long resumeId;
    private String portfolioName;

    @lombok.Setter
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PortfolioResponseDto from(Portfolio portfolio) {
        return PortfolioResponseDto.builder()
                .id(portfolio.getId())
                .resumeId(portfolio.getResume().getId())
                .portfolioName(portfolio.getPortfolioName())
                .url(portfolio.getUrl())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}

