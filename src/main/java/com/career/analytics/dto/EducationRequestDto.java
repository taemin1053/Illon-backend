package com.career.analytics.dto;

import com.career.analytics.entity.Education;
import com.career.analytics.entity.EducationStatus;
import com.career.analytics.entity.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EducationRequestDto {

    @NotBlank(message = "학교명은 필수입니다.")
    @Size(max = 20, message = "학교명은 20자 이내여야 합니다.")
    private String schoolName;

    @Size(max = 20, message = "전공은 20자 이내여야 합니다.")
    private String major;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "학적 상태(재학/휴학/졸업 등)를 선택해주세요.")
    private EducationStatus status;

    //  '이수 과목 및 연구 내용'을 받을 바구니 공간 추가!
    @Size(max = 2000, message = "이수 과목 및 연구 내용은 2000자 이내여야 합니다.")
    private String description;

    public Education toEntity(Resume resume) {
        return Education.builder()
                .resume(resume) // 양방향 연관관계 핵심
                .schoolName(this.schoolName)
                .major(this.major)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status)
                .description(this.description)
                .build();
    }

}