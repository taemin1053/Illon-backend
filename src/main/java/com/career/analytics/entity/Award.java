package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "awards")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id",nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 200, name = "award_name")
    private String awardName;

    @Column(length = 100, name = "organization")
    private String organization;

    @Column(name = "award_date")
    private LocalDate awardDate;

    // ✨ 완벽하게 들어간 description 필드
    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // create 정적 팩토리 메서드 추가
    public static Award create(Resume resume, String awardName, String organization, LocalDate awardDate, String description) {
        return Award.builder()
                .resume(resume)
                .awardName(awardName)
                .organization(organization)
                .awardDate(awardDate)
                .description(description)
                .build();
    }

    // ✨ 2. 파라미터가 4개(description 포함)로 늘어난 update 메서드!
    public void update(String awardName, String organization, LocalDate awardDate, String description) {
        if(awardName != null && !awardName.trim().isEmpty()){
            this.awardName = awardName;
        }
        if(organization != null && !organization.trim().isEmpty()){
            this.organization = organization;
        }
        if(awardDate != null){
            this.awardDate = awardDate;
        }
        if(description != null && !description.trim().isEmpty()){
            this.description = description;
        }
    }
    public void setResume(Resume resume) {
        this.resume = resume;
    }
}