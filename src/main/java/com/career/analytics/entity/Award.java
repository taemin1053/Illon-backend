package com.career.analytics.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalDate;
@Entity
@Table(name = "awards")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;

    @Column(nullable = false, length = 200, name = "award_name")
    private String awardName;

    @Column(length = 100, name = "organization")
    private String organization;

    @Column(name = "award_date")
    private LocalDate awardDate;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(String awardName, String organization, LocalDate awardDate) {
        if(awardName != null && !awardName.isEmpty()){
            this.awardName = awardName;
        }
        if(organization != null){
            this.organization = organization;
        }
        if(awardDate != null){
            this.awardDate = awardDate;
        }
    }

}
