package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkExperienceRequestDto {

    @NotBlank(message = "회사명은 필수입니다.")
    @Size(max = 100)
    private String companyName;

    @Size(max= 100)
    private String department; // 부서

    @Size(max = 100)
    private String role; //직무

    @NotNull(message = "시작일은 필수 입니다.")
    @PastOrPresent(message = "시작일은 미래의 날짜일 수 없습니다.")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent = false;


}
