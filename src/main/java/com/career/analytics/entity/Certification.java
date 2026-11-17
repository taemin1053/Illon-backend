package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✨ 원시 타입 long을 객체 타입 Long으로 수정!

    // ✨ 1. Users 도려내고 Resume 이식 완료
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 100, name = "certification_name")
    private String certificationName; // 자격증명

    @Column(length = 50, name = "issuing_organization")
    private String issuingOrganization; // 발급 기관

    @Column(name = "issue_date")
    private LocalDate issueDate; // 취득일

    @Column(name = "expiry_date")
    private LocalDate expiryDate; // 만료일

    // ✨ 2. UI 기획서에 있던 상세 내용 필드 추가!
    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at") // ✨ update_at 오타 수정!
    private LocalDateTime updatedAt;

    // ✨ 3. 누락되어 있던 정적 팩토리 메서드(create) 추가!
    public static Certification create(Resume resume, String certificationName, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate, String description) {
        return Certification.builder()
                .resume(resume)
                .certificationName(certificationName)
                .issuingOrganization(issuingOrganization)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .description(description) // 매핑
                .build();
    }

    // ✨ 4. 자격 정보 업데이트 메서드 (description 추가 및 StringUtils 적용)
    public void update(String certificationName, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate, String description) {
        if (StringUtils.isNotBlank(certificationName)) {
            this.certificationName = certificationName;
        }
        if (StringUtils.isNotBlank(issuingOrganization)) {
            this.issuingOrganization = issuingOrganization;
        }
        if (issueDate != null) {
            this.issueDate = issueDate;
        }
        if (expiryDate != null) {
            this.expiryDate = expiryDate;
        }
        if (StringUtils.isNotBlank(description)) {
            this.description = description;
        }
    }

    // ✨ 5. 양방향 연관관계를 위한 편의 메서드 추가
    public void setResume(Resume resume) {
        this.resume = resume;
    }
}