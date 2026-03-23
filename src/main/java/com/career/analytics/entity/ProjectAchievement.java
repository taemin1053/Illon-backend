package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_achievements")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class ProjectAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 200)
    private String projectName;

    @Column(length = 100)
    private String role; //역할(팀장, 디자이너, 개발자)

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description; //프로젝트 간단 설명

    @Column(columnDefinition = "TEXT")
    private String achievement; //성과, 결과

    @Column(name = "tech_stack", length = 500)
    private String techStack; //사용 기술 (쉼표 구분)

    @CreatedDate
    @Column(nullable = false,updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt;

    //프로젝트 성과 업데이트
    public void update(String projectName, String role, LocalDate startDate
            , LocalDate endDate, String description, String achievement, String techStack) {
        if( projectName != null && !projectName.isEmpty() ) {
            this.projectName = projectName;
        }
        if(role != null) {
            this.role = role;
        }
        if(startDate != null) {
            this.startDate = startDate;
        }
        if(endDate != null) {
            this.endDate = endDate;
        }
        if(description != null) {
            this.description = description;
        }
        if(achievement != null) {
            this.achievement = achievement;
        }
        if(techStack != null) {
            this.techStack = techStack;
        }
    }
}
