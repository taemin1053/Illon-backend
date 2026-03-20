package com.career.analytics.dto;

import com.career.analytics.entity.WorkExperience;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter

public class WorkExperienceResponseDto {

    private final Long id;
    private final Long userId;
    private final String companyName;
    private final String department;
    private final String role;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Boolean isCurrent;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public WorkExperienceResponseDto(WorkExperience workExperience) {
        this.id = workExperience.getId();
        this.userId = workExperience.getUser().getId();
        this.companyName = workExperience.getCompanyName();
        this.department = workExperience.getDepartment();
        this.role = workExperience.getRole();
        this.startDate = workExperience.getStartDate();
        this.endDate = workExperience.getEndDate();


        this.isCurrent = workExperience.isCurrent();

        this.createdAt = workExperience.getCreatedAt();
        this.updatedAt = workExperience.getUpdatedAt();
    }
}