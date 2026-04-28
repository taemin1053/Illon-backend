package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

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

    public void updatePortfolio(String portfolioName, String url ) {

        if(portfolioName != null && !portfolioName.isEmpty()){
            this.portfolioName = portfolioName;
        }
        if(url != null && !url.isEmpty()){
            this.url = url;
        }
    }
}
