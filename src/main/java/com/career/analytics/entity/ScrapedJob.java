package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate; // 💡 필수 임포트
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "scrap_jobs",
        uniqueConstraints = {
                // 중복 스크랩 방지: 유저 ID와 공고 ID의 조합은 테이블 내에서 유일해야 함
                @UniqueConstraint(columnNames = {"user_id", "job_id"})
        }
)
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 규격 상 기본 생성자 필수 제한
public class ScrapedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 스크랩을 누른 유저 객체 (지연 로딩 필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // AI 외부 서버에서 넘어오는 공고 고유 식별자 (UUID 문자열)
    @Column(name = "job_id", nullable = false)
    private String jobId;

    @Column(name = "deadline")
    private LocalDate deadline; // 💡 파싱된 마감일 (LocalDate)

    @Column(name = "job_created_at")
    private LocalDateTime jobCreatedAt; // 💡 파싱된 공고 생성일 (LocalDateTime)

    // 스크랩 일시 (Auditing 기능을 위한 어노테이션 명시)
    @CreatedDate // 💡 이 어노테이션이 있어야 스프링이 알아서 시간을 채워줍니다!
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 💡 생성 책임을 명확히 분리하는 정적 팩토리 메서드
    public static ScrapedJob createScrap(Users user, String jobId, LocalDate deadline, LocalDateTime jobCreatedAt) {
        ScrapedJob scrapedJob = new ScrapedJob();
        scrapedJob.user = user;
        scrapedJob.jobId = jobId;
        scrapedJob.deadline = deadline;
        scrapedJob.jobCreatedAt = jobCreatedAt;
        return scrapedJob;
    }
}