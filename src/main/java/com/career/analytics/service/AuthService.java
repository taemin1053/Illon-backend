package com.career.analytics.service;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.SignupResponseDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.RefreshToken; // 추가
import com.career.analytics.entity.Role;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.DuplicateEmailException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.ProfileRepository;
import com.career.analytics.repository.RefreshTokenRepository; // 추가
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.JwtTokenProvider;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository; // 1. 레포지토리 주입
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupDto) {
        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.: " + signupDto.getEmail());
        }

        Users user = Users.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .role(Role.USER)
                .build();
        Users savedUser = userRepository.save(user);

        Profile profile = Profile.createProfile(savedUser,
                signupDto.getName(), signupDto.getGender(),signupDto.getBirthDay(),
                signupDto.getBio(), signupDto.getPhone(), signupDto.getProfileImageUrl(),signupDto.getCareerLevel());
        profileRepository.save(profile);



        return new SignupResponseDto(user.getEmail(), user.getProfile().getName(),
                user.getCreatedAt());
    }

    /**
     *3. readOnly = true 제거
     * 이제 DB에 리프레시 토큰을 저장/수정해야 하므로 쓰기 권한이 필요합니다.
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);

        // 🚀 4. 기존 코드 버그 수정 (generateToken -> generateRefreshToken)
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Users user = userRepository.findById(userDetails.getId())
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userDetails.getId()));

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        String name = profile != null ? profile.getName() : null;

        // 🚀 5. 리프레시 토큰 Upsert (존재하면 수정, 없으면 생성)
        saveRefreshToken(user, refreshToken);

        return new TokenResponseDto(
                accessToken, refreshToken, user.getId(),
                user.getEmail(), name, user.getRole().name()
        );
    }

    /**
     * 6. 로그아웃 (DB에서 토큰 삭제)
     */
    @Transactional
    public void logout(Long userId) { // <-- 이렇게 Long 타입으로 변경!
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);
    }

     //리프레시 토큰 저장/수정 공통 로직 (Upsert)

    private void saveRefreshToken(Users user, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        existingToken -> existingToken.updateToken(token, expiresAt),
                        () -> {
                            RefreshToken newToken = RefreshToken.builder()
                                    .user(user)
                                    .token(token)
                                    .expiresAt(expiresAt)
                                    .build();
                            refreshTokenRepository.save(newToken);
                        }
                );
    }
}