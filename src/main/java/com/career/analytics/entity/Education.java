package com.career.analytics.entity;

import com.career.analytics.dto.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "educations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100, name = "school_name")
    private String schoolName;

    @Column(length = 100)
    private String major;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 20)
    private String status; // 재학, 졸업, 휴학 등

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 정적 팩토리 메서드 ==========

    /**
     * 학력 정보 생성
     */
    public static Education createEducation(Users user, EducationRequestDto dto) {
        return Education.builder()
                .user(user)
                .schoolName(dto.getSchoolName())
                .major(dto.getMajor())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus())
                .build();
    }

    // ========== 비즈니스 로직 메서드 ==========

    /**
     * 학력 정보 업데이트
     */
    public void update(EducationRequestDto dto) {
        if (dto.getSchoolName() != null && !dto.getSchoolName().isEmpty()) {
            this.schoolName = dto.getSchoolName();
        }
        if (dto.getMajor() != null) {
            this.major = dto.getMajor();
        }
        if (dto.getStartDate() != null) {
            this.startDate = dto.getStartDate();
        }
        if (dto.getEndDate() != null) {
            this.endDate = dto.getEndDate();
        }
        if (dto.getStatus() != null) {
            this.status = dto.getStatus();
        }
    }
}
