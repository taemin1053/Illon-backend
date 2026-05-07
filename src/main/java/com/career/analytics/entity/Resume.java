
package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이력서의 주인 (User)
     * 이름, 나이, 연락처 등 고정된 정보는 이 user 객체와 연결된 Profile에서 가져옴
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100)
    private String title; // 이력서 관리용 제목 (예: "네이버 지원용", "토스 지원용")

    @Builder.Default
    private Boolean isDefault = false; // 기본 이력서 여부

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 연관 관계 (이력서가 각 데이터의 생명주기(Lifecycle)를 소유) ==========

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Award> awards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios = new ArrayList<>(); // UI 하단의 포트폴리오/링크 리스트

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private CoverLetter coverLetter; // 자기소개서
    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    // ========== 연관 관계 편의 메서드 (객체 지향적 양방향 세팅) ==========

    public void addWorkExperience(WorkExperience experience) {
        this.workExperiences.add(experience);
        if (experience.getResume() != this) {
            experience.setResume(this);
        }
    }

    public void addEducation(Education education) {
        this.educations.add(education);
        if (education.getResume() != this) {
            education.setResume(this);
        }
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        if (portfolio.getResume() != this) {
            portfolio.setResume(this);
        }
    }

    public void setCoverLetter(CoverLetter coverLetter) {
        this.coverLetter = coverLetter;
        if (coverLetter.getResume() != this) {
            coverLetter.setResume(this);
        }
    }

    public void addCertification(Certification certification) {
        this.certifications.add(certification);
        if (certification.getResume() != this) {
            certification.setResume(this);
        }
    }

    // ========== 비즈니스 로직 (Business Logic) ==========

    public void updateTitle(String title) {
        if (StringUtils.hasText(title)) {
            this.title = title;
        }
    }

    public void setAsDefault() {
        this.isDefault = true;

    }

    public void unsetDefault() {
        this.isDefault = false;
    }
}
