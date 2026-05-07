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
    private final Long resumeId; // 1. userId 대신 resumeId로 변경
    private final String awardName;
    private final String organization;
    private final LocalDate awardDate;
    private final String description; // 2. 누락되었던 상세 내용 추가!
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static AwardResponseDto from(Award award) {
        return AwardResponseDto.builder()
                .id(award.getId())
                //  3. getUser() 대신 getResume()를 타고 가서 ID를 꺼냅니다.
                .resumeId(award.getResume().getId())
                .awardName(award.getAwardName())
                .organization(award.getOrganization())
                .awardDate(award.getAwardDate())
                .description(award.getDescription()) //  상세 내용 매핑 추가
                .createdAt(award.getCreatedAt())
                .updatedAt(award.getUpdatedAt())
                .build();
    }
}