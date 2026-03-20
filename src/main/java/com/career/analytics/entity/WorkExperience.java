package com.career.analytics.entity;

// ✅ DTO import 문을 완전히 날려버렸습니다! (순수한 도메인 유지)
import com.career.analytics.entity.Users;
import jakarta.persistence.*;
import lombok.*;
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
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100, name = "company_name")
    private String companyName;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String role;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default // ✅ 빌더 사용 시 기본값 유지
    @Column(nullable = false, name = "is_current")
    private boolean isCurrent = false; // ✅ null 위험이 없는 원시 타입(boolean)으로 변경

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 정적 팩토리 메서드 ==========


     //경력 정보 생성
    public static WorkExperience create(Users user, String companyName, String department,
                                        String role, LocalDate startDate, LocalDate endDate, boolean isCurrent) {
        return WorkExperience.builder()
                .user(user)
                .companyName(companyName)
                .department(department)
                .role(role)
                .startDate(startDate)
                .endDate(endDate)
                .isCurrent(isCurrent)
                .build();
    }

    // ========== 비즈니스 로직 메서드 ==========


     // 경력 정보 업데이트

    public void update(String companyName, String department, String role,
                       LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        if (companyName != null && !companyName.isEmpty()) {
            this.companyName = companyName;
        }
        if (department != null) {
            this.department = department;
        }
        if (role != null) {
            this.role = role;
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
    }
}