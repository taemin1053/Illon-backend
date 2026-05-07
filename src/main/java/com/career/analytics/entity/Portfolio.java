package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //long에서 객체 Long으로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 200)
    private String portfolioName;

    @Column(length = 500)
    private String url;

    @CreatedDate
    @Column(name = "created_at", updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(String portfolioName,String url ) {

        if(StringUtils.isNotBlank(portfolioName)){
            this.portfolioName = portfolioName;
        }
        if(StringUtils.isNotBlank(url)){
            this.url = url;
        }
    }
    public static Portfolio create(Resume resume,String portfolioName,String url) {
       return Portfolio.builder()
               .resume(resume)
               .portfolioName(portfolioName)
               .url(url)
               .build();
    }

    //양방향 연관관계를 위한 편의 메서드 추가
    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
