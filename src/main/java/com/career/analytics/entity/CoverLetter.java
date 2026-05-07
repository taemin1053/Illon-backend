package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cover_letters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✨ 핵심: 자기소개서는 이력서당 1개이므로 @OneToOne(일대일) 관계입니다!
    // 과거의 Users 잔재를 날리고 Resume를 완벽하게 이식했습니다.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 자기소개서 통짜 내용

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 정적 팩토리 메서드 ==========
    public static CoverLetter create(Resume resume, String content) {
        return CoverLetter.builder()
                .resume(resume)
                .content(content)
                .build();
    }

    // ========== 비즈니스 로직 ==========
    public void update(String content) {
        if (StringUtils.isNotBlank(content)) {
            this.content = content;
        }
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}