package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
//자격증
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
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100, name = "certification_name")
    private String certificationName; //자격증명

    @Column(length = 50, name = "issuing_organization")
    private String issuingOrganization; //발급 기관

    @Column(name = "issue_date")
    private LocalDate issueDate; //취득일

    @Column(name = "expiry_date")
    private LocalDate expiryDate; //만료일

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "update_at")
    private LocalDateTime updatedAt;

    //자격 정보 업데이트 메서드
    public void update(String certificationName, String issuingOrganization, LocalDate issueDate,
                       LocalDate expiryDate) {
        if(certificationName != null && !certificationName.isEmpty()) {
            this.certificationName = certificationName;
        }
        if(issuingOrganization != null) {
            this.issuingOrganization = issuingOrganization;
        }
        if(issueDate != null) {
            this.issueDate = issueDate;
        }
        if(expiryDate != null) {
            this.expiryDate = expiryDate;
        }
    }

}
