package com.career.analytics.dto;

import com.career.analytics.entity.WorkExperience;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class WorkExperienceResponseDto {

    private final Long id;
    private final Long resumeId;
    private final String companyName;
    private final String department;
    private final String role;

    // ✨ 1. 기획서에 맞춘 새로운 필드 2개 추가!
    private final String employmentType;
    private final String position;

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Boolean isCurrent;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String description;

    public WorkExperienceResponseDto(WorkExperience workExperience) {
        this.id = workExperience.getId();
        this.resumeId = workExperience.getResume().getId();
        this.companyName = workExperience.getCompanyName();
        this.department = workExperience.getDepartment();
        this.role = workExperience.getRole();

        // ✨ 2. 엔티티에서 꺼내와서 응답 바구니에 담아줍니다.
        this.employmentType = workExperience.getEmploymentType();
        this.position = workExperience.getPosition();

        this.startDate = workExperience.getStartDate();
        this.endDate = workExperience.getEndDate();
        this.description = workExperience.getDescription();
        this.isCurrent = workExperience.isCurrent(); // boolean 타입이므로 isCurrent() 사용
        this.createdAt = workExperience.getCreatedAt();
        this.updatedAt = workExperience.getUpdatedAt();
    }

    public static WorkExperienceResponseDto from(WorkExperience workExperience) {
        return new WorkExperienceResponseDto(workExperience);
    }
}