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
    private Role role = Role.USER; //관리자는 Update 수정해서 관리자 아이디 만들기

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
    // 1. 프로필
    private Profile profile;

    // 2. 인증 관련
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLogin> socialLogins = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;


    // 유저는 이제 이력서를 통해 자신의 경력/학력/자소서를 관리
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();

    //스크랩
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrapedJob> scrapedJobs = new ArrayList<>();
    // Users.java 파일 내부에 추가
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
