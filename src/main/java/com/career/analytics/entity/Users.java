package com.career.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") //DB Table name Users
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA를 위한 기본 생성자
@AllArgsConstructor (access = AccessLevel.PRIVATE) //Builder를 위한 전체. 생성자
@EntityListeners(AuditingEntityListener.class) // 스프링에서 jpa엔티티의 변경이나 저장을 감지해서 시간을 자동으로 넣음
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //엔티티에 저장할때 자동으로 기본키 생성 후 insert
    private Long id; //id is Pk

    @Column(nullable = false, unique = true, length = 255)
    private String email;


    private String password; //소설 로그인 사용자는 null 가능

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER; //관리자는 Update로 수정해서 관리자 아이디 만들기

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    //관계 설정하기
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    //profile에 있는 user필드에 매핑,
    //cascade 폭포수 효과 Users 객체를 저장(save)하거나 삭제(delete)할 때, 연결된 Profile 객체도 자동으로 같이 저장되거나 삭제
    //연결되지 않은 객체 삭제
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLogin> socialLogins = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Award> awards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrapedJob> scrapedJobs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoverLetter> coverLetters = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;
}
