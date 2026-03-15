package com.career.analytics.dto;


import com.career.analytics.entity.ProjectAchievement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

//요청 DTO
@Getter
@Setter
public class ProjectAchievementRequestDto {
    @NotBlank(message = "프로젝트명을 입력하세요")
    @Size(max = 100)
    private String projectName;

    @Size(max = 100, message = "역할을 100자 이내로 입력하세요")
    private String role;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 2000, message = "프로젝트 설명을 해주세요")
    private String description;

    @Size(max = 2000, message = "성과를 입력해주세요")
    private String achievement;

    @Size(max = 300, message = "사용한 기술을 작성해주세요")
    private String techStack;
}

//응답 DTO
@Getter
//@Setter
class ProjectAchievementResponseDto {
    private final Long id;
    private final Long userId;
    private final String projectName;
    private final String role;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;
    private final String achievement;
    private final String techStack;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProjectAchievementResponseDto(ProjectAchievement project) {
        this.id = project.getId();
        this.userId = project.getUser().getId();
        this.projectName = project.getProjectName();
        this.role = project.getRole();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.description = project.getDescription();
        this.achievement = project.getAchievement();
        this.techStack = project.getTechStack();
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }
}