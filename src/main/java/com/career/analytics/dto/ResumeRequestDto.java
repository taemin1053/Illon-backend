package com.career.analytics.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeRequestDto {

    @NotBlank(message = "이력서 제목은 필수입니다.")
    private String title;

    private Boolean isDefault;

    //하위 도메인들 DTO로 묶어서 관리하기
    @Valid
    private List<WorkExperienceRequestDto> workExperiences;

    @Valid
    private List<EducationRequestDto> educations;

    @Valid
    private List<AwardRequestDto> awards;

    @Valid
    private List<LanguageRequestDto> languages;

    @Valid
    private List<PortfolioRequestDto> portfolios;

    //자소서 ->coverletter은 1대1임
    @Valid
    private CoverLetterRequestDto coverLetter;
    @Valid

    private List<CertificationRequestDto> certifications;

}
