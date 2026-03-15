package com.career.analytics.dto;

import com.career.analytics.entity.Gender;
import com.career.analytics.entity.Profile;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String bio;
    private LocalDateTime updateDate;

    public ProfileResponseDto(Profile profile) {
        this.id = profile.getId();
        this.userId = profile.getUser().getId();
        this.name = profile.getName();
        this.gender = profile.getGender();
        this.birthDate = profile.getBirthDate();
        this.bio = profile.getBio();
        this.updateDate = profile.getUpdatedAt();
    }
}
