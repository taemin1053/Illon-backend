package com.career.analytics.service;


import com.career.analytics.dto.ProfileResponseDto;
import com.career.analytics.dto.ProfileUpdateRequestDto;
import com.career.analytics.dto.UserFullProfileResponseDto;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.ProfileNotFoundException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.ProfileRepository;
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getMyProfile() {
        Long userId = getCurrentUserId();

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()->new ProfileNotFoundException(userId));

        return new ProfileResponseDto(profile);
    }

    @Transactional(readOnly = true)
    public UserFullProfileResponseDto getMyFullProfile() {
        Long userId = getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(userId));
        Profile profile =profileRepository.findByUserId(userId).orElse(null);

        return new UserFullProfileResponseDto(user, profile);
    }

    @Transactional
    public ProfileResponseDto updateProfile(ProfileUpdateRequestDto profileDto){
        Long userId = getCurrentUserId();

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()->new ProfileNotFoundException(userId));

        profile.updateProfile(
                profileDto.getName(),
                profileDto.getGender(),
                profileDto.getBirthDate(),
                profileDto.getBio()
        );

        return new ProfileResponseDto(profile);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
