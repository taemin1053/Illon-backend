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
@Table(name = "work_experiences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 100, name = "company_name")
    private String companyName;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String role; // 직무 (예: ux/ui디자이너)

    // ✨ 1. 기획서에 맞춰 추가된 필드들!
    @Column(length = 50, name = "employment_type")
    private String employmentType; // 재직형태 (예: 정규직, 계약직, 인턴)

    @Column(length = 50)
    private String position; // 직책 (예: 사원, 대리, 팀장)

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(nullable = false, name = "is_current")
    private boolean isCurrent = false;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✨ 2. 정적 팩토리 메서드에 파라미터 추가
    public static WorkExperience create(Resume resume, String companyName, String department,
                                        String role, String employmentType, String position,
                                        LocalDate startDate, LocalDate endDate, boolean isCurrent, String description) {
        return WorkExperience.builder()
                .resume(resume)
                .companyName(companyName)
                .department(department)
                .role(role)
                .employmentType(employmentType) // 매핑
                .position(position)             // 매핑
                .startDate(startDate)
                .endDate(endDate)
                .isCurrent(isCurrent)
                .description(description)
                .build();
    }

    // ✨ 3. 비즈니스 로직(update) 메서드에 파라미터 추가
    public void update(String companyName, String department, String role,
                       String employmentType, String position,
                       LocalDate startDate, LocalDate endDate, Boolean isCurrent, String description) {
        if (StringUtils.isNotBlank(companyName)) {
            this.companyName = companyName;
        }
        if (StringUtils.isNotBlank(department)) {
            this.department = department;
        }
        if (StringUtils.isNotBlank(role)) {
            this.role = role;
        }
        // ✨ 추가된 필드 더티체킹 로직
        if (StringUtils.isNotBlank(employmentType)) {
            this.employmentType = employmentType;
        }
        if (StringUtils.isNotBlank(position)) {
            this.position = position;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if (isCurrent != null) {
            this.isCurrent = isCurrent;
            if (this.isCurrent) {
                this.endDate = null;
            }
        }
        if (StringUtils.isNotBlank(description)) {
            this.description = description;
        }
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}