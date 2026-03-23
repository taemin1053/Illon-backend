package com.career.analytics.dto;

import com.career.analytics.entity.ProjectAchievement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;
@Getter
@Setter
public class ProjectAchievementResponseDto {

    private Long id;
    private Long userId;
    private String projectName;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String techStack;
    private String achievement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
