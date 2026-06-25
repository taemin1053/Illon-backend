package com.career.analytics.dto;

import com.career.analytics.entity.CareerLevel;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.Users;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public record MyPageResponseDto(
    UserProfileDto profile,
    Page<ScrapedJobResponseDto> scrapedJobs
) {
    public record UserProfileDto(
        Long userId,
        String email,
        String name,
        String phone,
        String profileImageUrl,
        String careerLevel,
        String careerLevelKorean,  // UI 뱃지/드롭다운 노출용 ("신입", "경력", "인턴")
        String birthDate,          // "yyyy.MM.dd" (예: "2000.02.21")
        int age                    // 만 나이 (예: 25)
    ) {
        public static UserProfileDto from(Users user) {
            Profile profile = user.getProfile();
            
            if (profile == null) {
                return new UserProfileDto(
                        user.getId(), user.getEmail(), "이름 없음", "", "",
                        CareerLevel.NEWCOMER.name(), "신입", "", 0
                );
            }

            String formattedBirth = "";
            int calculatedAge = 0;
            if (profile.getBirthDate() != null) {
                formattedBirth = profile.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                calculatedAge = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
            }

            String careerLevelKorean = "미설정";
            if (profile.getCareerLevel() != null) {
                careerLevelKorean = switch (profile.getCareerLevel()) {
                    case NEWCOMER -> "신입";
                    case EXPERIENCED -> "경력";
                    case INTERN -> "인턴";
                };
            }

            return new UserProfileDto(
                    user.getId(),
                    user.getEmail(),
                    profile.getName() != null ? profile.getName() : "이름 없음",
                    profile.getPhone() != null ? profile.getPhone() : "",
                    profile.getProfileImageUrl() != null ? profile.getProfileImageUrl() : "",
                    profile.getCareerLevel() != null ? profile.getCareerLevel().name() : null,
                    careerLevelKorean,
                    formattedBirth,
                    calculatedAge
            );
        }
    }
}
