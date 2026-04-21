package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 50, name = "language_name")
    private String languageName; // 언어명 (영어, 일본어, 중국어 등)


    @Column(length = 50, name = "test_name")
    private String testName; // 시험명 (TOEIC, TOEFL, JLPT, HSK 등)


    @Column(length = 50)
    private String score; // 점수 (900점, Level 2 등)


    @Column(name = "test_date")
    private LocalDate testDate; // 시험일


    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;


    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;


// ========== 비즈니스 로직 메서드 ==========

    public void update(String languageName, String testName, String score, LocalDate testDate) {

        if (languageName != null && !languageName.isEmpty()) {

            this.languageName = languageName;

        }

        if (testName != null) {

            this.testName = testName;

        }
    }
}