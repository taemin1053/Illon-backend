package com.career.analytics.dto;

import com.career.analytics.entity.Education;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//응답 DTO
@Getter
@Setter
public class EducationResponseDto {

    private Long id;
    private Long userId;
    private String schoolName;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public EducationResponseDto(Education education) {
        this.id = education.getId();
        this.userId = education.getUser().getId();
        this.schoolName = education.getSchoolName();
        this.major = education.getMajor();
        this.startDate = education.getStartDate();
        this.endDate = education.getEndDate();
        this.status = education.getStatus();
    }
}