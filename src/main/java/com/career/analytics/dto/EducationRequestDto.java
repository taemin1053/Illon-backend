package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
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

    @Size(max = 20, message = "재학, 휴학, 졸업")
    private String status;

}