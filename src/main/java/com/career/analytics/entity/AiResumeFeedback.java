package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "ai_resume_feedbacks")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiResumeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_resume_feedback_id")
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    /**
     * 💡 Hibernate 6 기준 PostgreSQL JSONB 타입 매핑 설정
     * DB에는 jsonb 컬럼으로 매핑되며, 자바 레벨에서는 JSON 문자열로 핸들링합니다.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "feedback_result", columnDefinition = "jsonb", nullable = false)
    private String feedbackResult;

    // 비즈니스 편의 메서드 (피드백 결과 업데이트)
    public void updateFeedbackResult(String feedbackResult) {
        if (feedbackResult != null && !feedbackResult.isBlank()) {
            this.feedbackResult = feedbackResult;
        }
    }
}
