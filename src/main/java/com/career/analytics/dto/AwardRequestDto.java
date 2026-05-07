package com.career.analytics.dto;

import com.career.analytics.entity.Award;
import com.career.analytics.entity.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AwardRequestDto {

    @NotBlank(message = "수상명은 필수입니다.")
    @Size(max = 200, message = "수상명은 200자 이내여야 합니다.")
    private String awardName;

    @Size(max = 100, message = "수여 기관은 100자 이내여야 합니다.")
    private String organization;

    private LocalDate awardDate;

    // '상세 내용(부문 입선 등)'을 받기 위한 필드 추가
    @Size(max = 1000, message = "상세 내용은 1000자 이내여야 합니다.")
    private String description;

    public Award toEntity(Resume resume) {
        return Award.builder()
                .resume(resume)
                .awardName(this.awardName)
                .organization(this.organization)
                .awardDate(this.awardDate)
                .description(this.description)
                .build();
    }
}