package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "employment_preferences")
@Getter
@Setter
@NoArgsConstructor

public class EmploymentPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    //국가 유공자, 장애, 군대 여부
    @Column(name = "veterans_status", columnDefinition = "boolean default false")
    private boolean veteransStatuus = false;

    @Column(name = "disability_status", columnDefinition = "boolean default false")
    private Boolean disabilityStatus = false;

    @Column(name = "military_service", length = 50)
    private String militaryService;
}
