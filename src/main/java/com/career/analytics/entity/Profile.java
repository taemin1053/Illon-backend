package com.career.analytics.entity;

import com.career.analytics.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
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
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false, length = 50)
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

    //profile 생성자
    public static Profile createProfile(Users user, SignupRequestDto signupDto) {
        return Profile.builder()
                .user(user)
                .name(signupDto.getName())
                .gender(signupDto.getGender())
                .birthDate(signupDto.getBirthDay())
                .bio(signupDto.getBio())
                .build();
    }
    //profile update 메서드
    public void updateProfile(String name, Gender gender, LocalDate birthDate, String bio) {
        if(name != null && !name.isEmpty()){
            this.name = name;
        }
        if(gender !=null){
            this.gender = gender;
        }
        if(birthDate != null){
            this.birthDate = birthDate;
        }
        if(bio != null){
            this.bio = bio;
        }
    }

}
