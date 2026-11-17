package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.apache.commons.lang3.StringUtils;
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
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 100, name = "school_name")
    private String schoolName;

    @Column(length = 100)
    private String major;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 20)
    //Enum 타입으로 변경
    @Enumerated(EnumType.STRING)
    private EducationStatus status;
    //private String status; // 재학, 졸업, 휴학 등

    // ✨ 기획서의 '이수 과목 또는 연구 내용'을 담을 필드 추가!
    @Column(columnDefinition = "TEXT")
    private String description;

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
    //DTO를 통째로 받지 않고 파라미터만 받도록 수정
    public static Education createEducation(Resume resume, String schoolName, String major, LocalDate startDate, LocalDate endDate, EducationStatus status, String description) {
        return Education.builder()
                .resume(resume)
                .schoolName(schoolName)
                .major(major)
                .startDate(startDate)
                .endDate(endDate)
                .status(status)
                .description(description) // ✨ 매핑
                .build();
    }

    // ========== 비즈니스 로직 메서드 ==========

    // 학력 정보 업데이트
    //StringUtils.isNotBlank -> " ", 공백, null이면 false 리턴
    public void update(String schoolName, String major, LocalDate startDate, LocalDate endDate, EducationStatus status, String description) {
        if (StringUtils.isNotBlank(schoolName)) {
            this.schoolName = schoolName;
        }
        if (StringUtils.isNotBlank(major)) {
            this.major = major;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if (status != null) {
            this.status = status;
        }
        // ✨ 추가된 description 더티체킹 로직
        if (StringUtils.isNotBlank(description)) {
            this.description = description;
        }
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}