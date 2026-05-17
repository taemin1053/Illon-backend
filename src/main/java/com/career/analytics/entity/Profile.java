package com.career.analytics.entity;


import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "profiles")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Profile {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name = "profiles_id" , nullable = false, unique = true)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column( nullable = true, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String bio; //자기 소개란

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private String phone;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Builder.Default // 빌더 패턴을 사용한다면 필수
    @Enumerated(EnumType.STRING)
    @Column(name = "career_level", nullable = false)
    private CareerLevel careerLevel = CareerLevel.NEWCOMER;

    //profile 생성자
    public static Profile createProfile(Users user, String name, Gender gender,
                                        LocalDate birthDate, String bio, String phone, String profileImageUrl, CareerLevel careerLevel) {
        return Profile.builder()
                .user(user)
                .name(name)
                .gender(gender)
                .birthDate(birthDate)
                .bio(bio)
                .phone(phone)
                .profileImageUrl(profileImageUrl)
                .careerLevel(careerLevel)
                .build();
    }
    //profile update 메서드 -> 문자열 검증 업그레이드 StringUtils
    public void updateProfile(String name, Gender gender, LocalDate birthDate,
                              String bio, String phone, String profileImageUrl, CareerLevel careerLevel) {

        // 1. 문자열 (String) 방어: null 아님 + 공백만 있는 게 아님
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (bio != null && !bio.trim().isEmpty()) {
            this.bio = bio;
        }
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        }
        if (profileImageUrl != null && !profileImageUrl.trim().isEmpty()) {
            this.profileImageUrl = profileImageUrl;
        }

        // 2. 객체 타입 (Enum, LocalDate) 방어: null 아님
        if (gender != null) {
            this.gender = gender;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (careerLevel != null) {
            this.careerLevel = careerLevel;
        }
    }
    public static Profile createEmptyProfile(Users user) {
        return Profile.builder()
                .user(user)
                .build();
    }
    public void updateVerificationInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

}
