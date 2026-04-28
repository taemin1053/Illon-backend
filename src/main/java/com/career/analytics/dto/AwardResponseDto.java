package com.career.analytics.dto;

import com.career.analytics.entity.Award;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AwardResponseDto {
    private final Long id;
    private final Long userId;
    private final String awardName;
    private final String organization;
    private final LocalDate awardDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static AwardResponseDto from(Award award) {
        return AwardResponseDto.builder()
                .id(award.getId())
                .userId(award.getUser().getId())
                .awardName(award.getAwardName())
                .organization(award.getOrganization())
                .awardDate(award.getAwardDate())
                .createdAt(award.getCreatedAt())
                .build();
    }
}
