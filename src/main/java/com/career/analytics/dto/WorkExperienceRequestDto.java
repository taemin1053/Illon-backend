package com.career.analytics.dto;

import com.career.analytics.entity.Resume;
import com.career.analytics.entity.WorkExperience;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceRequestDto {

    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 100, message = "회사명은 100자 이내여야 합니다")
    private String companyName;

    @Size(max = 100, message = "부서명은 100자 이내여야 합니다")
    private String department;

    @Size(max = 100, message = "직무는 100자 이내여야 합니다")
    private String role;

    // . 기획서에 맞춰 방금 엔티티에 추가했던 2개의 필드 추가!
    @Size(max = 50, message = "재직형태는 50자 이내여야 합니다")
    private String employmentType;

    @Size(max = 50, message = "직책은 50자 이내여야 합니다")
    private String position;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    private LocalDate endDate;

    // ✨ 2. 주요 성과 텍스트 방어막 추가
    @Size(max = 2000, message = "주요 성과는 2000자 이내여야 합니다")
    private String description;

    private Boolean isCurrent = false; // 현재 재직 중

    public WorkExperience toEntity(Resume resume) {
        return WorkExperience.builder()
                .resume(resume)
                .companyName(this.companyName)
                // DTO의 변수명(this.department)을 엔티티의 필드명에 맞게 매핑
                .department(this.department)
                .role(this.role)
                // 새로 추가하신 2개 필드 조립!
                .employmentType(this.employmentType)
                .position(this.position)
                // DTO의 변수명(this.description)을 매핑
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isCurrent(this.isCurrent)
                .build();
    }
}