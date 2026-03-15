package com.career.analytics.dto;

import com.career.analytics.entity.Education;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
//요청 DTO
@Getter
@Setter
class EducationRequestDto {

    @NotBlank(message = "학교명은 필수입니다.")
    @Size(max = 20, message = "학교명은 20자 이내여야 합니다.")
    private String schoolName;

    @Size(max = 20, message = "전공은 20자 이내여야 합니다.")
    private String major;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 20, message = "재학, 휴학, 졸업")
    private String status;

}

//응답 DTO
@Getter
@Setter
class EducationResponseDto {

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