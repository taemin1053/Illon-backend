package com.career.analytics.dto;

import com.career.analytics.entity.Gender;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.Users;
import lombok.Getter;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserFullProfileResponseDto {

    //user 정보
    private Long userId;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //profile 정보
    private Boolean hasProfile;
    private Long profileId;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String bio;
    private LocalDateTime profileUpdatedAt;
    private String profileImageUrl;
    private String phone;

    public UserFullProfileResponseDto(Users user, Profile profile) {
        //Users 정보
        this.userId = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        //profile 정보
        if(profile != null) {
            this.hasProfile = true;
            this.profileId = profile.getId();
            this.name = profile.getName();
            this.gender = profile.getGender();
            this.birthDate = profile.getBirthDate();
            this.bio = profile.getBio();
            this.profileUpdatedAt = profile.getUpdatedAt();
            this.profileImageUrl = profile.getProfileImageUrl();
            this.phone = profile.getPhone();
        } else {
            this.hasProfile = false;
        }
    }
}
