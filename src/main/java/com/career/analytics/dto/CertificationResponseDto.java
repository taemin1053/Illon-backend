package com.career.analytics.dto;

import com.career.analytics.entity.Certification;
import com.career.analytics.entity.Education;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 유지
public class CertificationResponseDto {

    private Long id;
    // 1. 옛날 잔재인 userId를 지우고 resumeId로 교체!
    private Long resumeId;
    private String certificationName;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    // ✨ 2. 기획서에 맞춰 추가한 상세 내용 바구니!
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티를 DTO로 변환하는 생성자
    public CertificationResponseDto(Certification certification) {
        this.id = certification.getId();
        // ✨ 3. 빨간 줄의 원인 해결: getUser() 대신 getResume() 사용
        this.resumeId = certification.getResume().getId();
        this.certificationName = certification.getCertificationName();
        this.issuingOrganization = certification.getIssuingOrganization();
        this.issueDate = certification.getIssueDate();
        this.expiryDate = certification.getExpiryDate();

        // ✨ 4. 엔티티에서 상세 내용을 꺼내서 응답으로 내려줍니다.
        this.description = certification.getDescription();

        this.createdAt = certification.getCreatedAt();
        this.updatedAt = certification.getUpdatedAt();
    }


    public static CertificationResponseDto from(Certification certification) {
        return new CertificationResponseDto(certification);

    }
}