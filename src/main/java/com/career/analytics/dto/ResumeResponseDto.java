package com.career.analytics.dto;

import com.career.analytics.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponseDto {
    private Long id;
    private String title;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<WorkExperienceResponseDto> workExperiences;
    private List<EducationResponseDto> educations;
    private List<AwardResponseDto> awards;
    private List<LanguageResponseDto> languages;
    private List<PortfolioResponseDto> portfolios;
    private CoverLetterResponseDto coverLetter;
    private List<CertificationResponseDto> certifications;
    //엔티티를 DTO로 변환하는 정적 펙토리 메서드
    //이렇게 하는 이유: JPA 양방향 연관 관계에서 엔티티를 직접 반환하면 JSON 반환 시 무한 루프에 빠져 서버 다운 방지
    // 프론트가 요구하는 데이터 형식이 바뀌어도 엔티티는 건드리지 않고 DTO만 수정 가능
    public static ResumeResponseDto from(Resume resume) {
        return ResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .isDefault(resume.getIsDefault())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())

                .workExperiences(resume.getWorkExperiences().stream()
                        .map(WorkExperienceResponseDto::from)
                        .collect(Collectors.toList()))
                .educations(resume.getEducations().stream()
                        .map(EducationResponseDto::from)
                        .collect(Collectors.toList()))

                .awards(resume.getAwards().stream()
                        .map(AwardResponseDto::from)
                        .collect(Collectors.toList()))

                .languages(resume.getLanguages().stream()
                        .map(LanguageResponseDto::from)
                        .collect(Collectors.toList()))

                .portfolios(resume.getPortfolios().stream()
                        .map(PortfolioResponseDto::from)
                        .collect(Collectors.toList()))
                .certifications(resume.getCertifications().stream()
                        .map(CertificationResponseDto::from)
                        .collect(Collectors.toList()))
                // 1:1 관계이므로 null 체크 필수
                .coverLetter(resume.getCoverLetter() != null ?
                        CoverLetterResponseDto.from(resume.getCoverLetter()) : null)
                .build();

    }
}
