package com.career.analytics.service;

import com.career.analytics.dto.*;
import com.career.analytics.entity.*;
import com.career.analytics.exception.DuplicateEmailException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.*;
import com.career.analytics.security.JwtTokenProvider;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialLoginRepository socialLoginRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PortOneService portOneService;

    @Transactional
    public TokenResponseDto emailSignup(SignupRequestDto signupDto, String verificationId) {

        // 1. 포트원 인증 정보 조회
        Map<String, Object> response = portOneService.getIdentityVerification(verificationId);
        Map<String, Object> customer = (Map<String, Object>) response.get("verifiedCustomer");

        String ci = (String) customer.get("ci");
        String name = (String) customer.get("name");
        String phone = (String) customer.get("phoneNumber");

        if (ci == null) {
            log.warn("포트원 응답에 CI 값이 없습니다. 임시 CI를 대체 사용합니다.");
            if (phone == null || phone.isEmpty()) {
                throw new RuntimeException("테스트 환경 오류: 포트원 응답에 전화번호조차 없습니다.");
            }
            ci = "TEST_CI_" + phone;
        }
        String birthDateStr = (String) customer.get("birthDate");
        String genderStr = (String) customer.get("gender");

        LocalDate birthDate = birthDateStr != null ? LocalDate.parse(birthDateStr) : null;
        Gender gender = genderStr != null ? Gender.valueOf(genderStr) : null;

        // 2. DB에 동일한 명의(CI)가 있는지 조회
        Optional<Users> existingUserOpt = userRepository.findByCi(ci);

        Users user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            log.info("✅ 기존 소셜/일반 계정 발견. 이메일 가입 정보를 연동(Merge)합니다. User ID: {}", user.getId());

            if (!user.getEmail().equals(signupDto.getEmail())) {
                if (userRepository.existsByEmail(signupDto.getEmail())) {
                    throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + signupDto.getEmail());
                }
                user.updateEmail(signupDto.getEmail());
            }
            user.updatePassword(passwordEncoder.encode(signupDto.getPassword()));

            Profile profile = user.getProfile();
            if (profile != null) {
                profile.updateVerificationInfo(name, phone);
            }
        } else {
            if (userRepository.existsByEmail(signupDto.getEmail())) {
                throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + signupDto.getEmail());
            }

            user = Users.builder()
                    .email(signupDto.getEmail())
                    .password(passwordEncoder.encode(signupDto.getPassword()))
                    .role(Role.USER)
                    .ci(ci)
                    .build();

            Profile profile = Profile.builder()
                    .user(user)
                    .name(name)
                    .phone(phone)
                    .birthDate(birthDate)
                    .gender(gender)
                    .build();

            user.setProfile(profile);
            userRepository.save(user);
            log.info("✅ 완전 신규 이메일 가입 완료. Email: {}", user.getEmail());
        }

        return createTokenResponse(user, name);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        Users user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        log.info("✅ 이메일 로그인 성공: {}, 설문완료여부: {}", user.getEmail(), user.isSurveyCompleted());

        return createTokenResponse(user, user.getProfile().getName());
    }

    @Transactional
    public TokenResponseDto integrateSocialAccount(Long tempUserId, String verificationId) {
        Map<String, Object> response = portOneService.getIdentityVerification(verificationId);
        Map<String, Object> customer = (Map<String, Object>) response.get("verifiedCustomer");

        String ci = (String) customer.get("ci");
        String name = (String) customer.get("name");
        String phone = (String) customer.get("phoneNumber");

        if (ci == null) {
            ci = "TEST_CI_" + phone;
        }

        Users tempUser = userRepository.findById(tempUserId)
                .orElseThrow(() -> new UserNotFoundException("임시 사용자를 찾을 수 없습니다."));

        Optional<Users> existingUserOpt = userRepository.findByCi(ci);
        Users finalUser;

        if (existingUserOpt.isPresent()) {
            Users existingUser = existingUserOpt.get();
            log.info("✅ 기존 정식 회원 확인됨. 계정 통합(Merge)을 시작합니다. User ID: {}", existingUser.getId());

            List<SocialLogin> oldSocialLogins = socialLoginRepository.findByUserId(tempUser.getId());
            List<String[]> backups = oldSocialLogins.stream()
                    .map(sl -> new String[]{sl.getProvider(), sl.getProviderId()})
                    .toList();

            userRepository.delete(tempUser);
            userRepository.flush();

            for (String[] backup : backups) {
                SocialLogin newSl = SocialLogin.builder()
                        .user(existingUser)
                        .provider(backup[0])
                        .providerId(backup[1])
                        .build();
                socialLoginRepository.save(newSl);
            }
            finalUser = existingUser;
        } else {
            tempUser.updateCi(ci);
            Profile profile = tempUser.getProfile();
            if (profile != null) {
                profile.updateVerificationInfo(name, phone);
            }
            finalUser = tempUser;
        }

        return createTokenResponse(finalUser, finalUser.getProfile().getName());
    }

    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 존재하지 않습니다."));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인하세요.");
        }

        return createTokenResponse(tokenEntity.getUser(), tokenEntity.getUser().getProfile().getName());
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);
    }

    // 🔥 핵심: 모든 토큰 응답을 여기서 생성하여 일관성 유지
    private TokenResponseDto createTokenResponse(Users user, String name) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        saveRefreshToken(user, refreshToken);

        return new TokenResponseDto(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                name,
                user.getRole().name(),
                user.isSurveyCompleted() // 👈 완벽하게 전달됨!
        );
    }

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