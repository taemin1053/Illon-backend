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
@Table(name = "languages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✨ 1. Users를 도려내고 Resume를 이식했습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 50, name = "language_name")
    private String languageName; // 언어명 (영어, 일본어, 중국어 등)

    @Column(length = 50, name = "test_name")
    private String testName; // 시험명 (TOEIC, TOEFL, JLPT, HSK 등)

    @Column(length = 50)
    private String score; // 점수 (900점, Level 2 등)

    @Column(name = "test_date")
    private LocalDate testDate; // 시험일

    // ✨ 2. 기획서의 '상세 내용'을 담을 필드 추가! (이수 과목 문구 있던 그 자리)
    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 정적 팩토리 메서드 ==========

    public static Language create(Resume resume, String languageName, String testName, String score, LocalDate testDate, String description) {
        return Language.builder()
                .resume(resume)
                .languageName(languageName)
                .testName(testName)
                .score(score)
                .testDate(testDate)
                .description(description)
                .build();
    }

    // ========== 비즈니스 로직 메서드 ==========

    public void update(String languageName, String testName, String score, LocalDate testDate, String description) {
        if (StringUtils.isNotBlank(languageName)) {
            this.languageName = languageName;
        }
        if (StringUtils.isNotBlank(testName)) {
            this.testName = testName;
        }
        if (StringUtils.isNotBlank(score)) {
            this.score = score;
        }
        if (testDate != null) {
            this.testDate = testDate;
        }
        // ✨ 상세 내용 업데이트 로직 추가
        if (StringUtils.isNotBlank(description)) {
            this.description = description;
        }
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}