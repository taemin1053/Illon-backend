package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectAchievementRequestDto {

    @NotBlank(message = "프로젝트명은 필수 입니다")
    @Size(max = 200, message = "프로젝트명은 200자 이내여야합니다.")
    private String projectName;

    @Size(max= 100, message = "역할은 100자 이내여야합니다")
    private String role;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 2000, message = "2000자 이내로 프로젝트 성명을 작성해주세요")
    private String description;

    @Size(max = 2000, message = "2000자 이내로 성과를 작성해주세요")
    private String achievement;

    @Size(max = 500, message = "사용하신 기술을 500자 이내로 작성해 주세요")
    private String techStack;
}
