package com.career.analytics.dto;

import com.career.analytics.entity.Certification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // ✅ 기본 생성자도 하나 추가해두면 직렬화 시 안전합니다.
public class CertificationResponseDto {

    private Long id;
    private Long userId;
    private String certificationName;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티를 DTO로 변환하는 생성자
    public CertificationResponseDto(Certification certification) {
        this.id = certification.getId();
        this.userId = certification.getUser().getId();
        this.certificationName = certification.getCertificationName();
        this.issuingOrganization = certification.getIssuingOrganization();


        this.issueDate = certification.getIssueDate();
        this.expiryDate = certification.getExpiryDate();


        this.createdAt = certification.getCreatedAt();
        this.updatedAt = certification.getUpdatedAt();
    }
}