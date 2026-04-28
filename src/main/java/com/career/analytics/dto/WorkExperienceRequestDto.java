package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class WorkExperienceRequestDto {

    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 100, message = "회사명은 100자 이내여야 합니다")
    private String companyName;

    @Size(max = 100, message = "부서명은 100자 이내여야 합니다")
    private String department;

    @Size(max = 100, message = "직무는 100자 이내여야 합니다")
    private String role;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private final Boolean isCurrent = false; // 현재 재직 중
}
