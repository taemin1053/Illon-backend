package com.career.analytics.dto;

import com.career.analytics.entity.Certification;
import com.career.analytics.entity.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CertificationRequestDto {

    @NotBlank(message = "자격증명은 필수입니다")
    @Size(max = 100, message = "자격증명은 100자 이내여야 합니다")
    private String certificationName;

    @Size(max = 100, message = "발급기관은 100자 이내여야 합니다")
    private String issuingOrganization;

    private LocalDate issueDate; // 취득일

    private LocalDate expiryDate; // 만료일 (없으면 null)

    // ✨ 기획서에 있던 상세 내용(부문 입선, 점수 등)을 받을 바구니 공간 추가!
    @Size(max = 2000, message = "상세 내용은 2000자 이내여야 합니다")
    private String description;

    public Certification toEntity(Resume resume) {
        return Certification.builder()
                .resume(resume)
                .certificationName(this.certificationName)
                .issueDate(this.issueDate)
                .expiryDate(this.expiryDate)
                .description(this.description)
                .issuingOrganization(this.issuingOrganization)
                .build();
    }
}