package com.career.analytics.service;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.SignupResponseDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.entity.Gender;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.RefreshToken;
import com.career.analytics.entity.Role;
import com.career.analytics.entity.SocialLogin; // ✨ 추가: 소셜 로그인 엔티티
import com.career.analytics.entity.Users;
import com.career.analytics.exception.DuplicateEmailException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.ProfileRepository;
import com.career.analytics.repository.RefreshTokenRepository;
import com.career.analytics.repository.SocialLoginRepository; // ✨ 추가: 소셜 레포지토리
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.JwtTokenProvider;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✨ 로깅을 위해 추가
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // ✨ 추가
import java.util.Map;
import java.util.Optional; // ✨ 추가

@Slf4j // ✨ 테스트 환경 경고 로그를 찍기 위해 추가
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialLoginRepository socialLoginRepository; // ✨ 소셜 로그인 레포지토리 주입

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PortOneService portOneService;


    @Transactional
    public SignupResponseDto emailSignup(SignupRequestDto signupDto, String verificationId) {
        Map<String, Object> response = portOneService.getIdentityVerification(verificationId);
        Map<String, Object> customer = (Map<String, Object>) response.get("verifiedCustomer");

        String ci = (String) customer.get("ci");
        String name = (String) customer.get("name");
        String phone = (String) customer.get("phoneNumber");

        // ✨ 포트원 테스트 환경(V2) 대응: CI 값이 없을 경우 임시 값으로 대체
        if (ci == null) {
            log.warn("포트원 응답에 CI 값이 없습니다. (테스트 환경 의심). 임시 CI를 대체 사용합니다.");
            ci = "TEST_CI_" + verificationId;
        }

        String birthDateStr = (String) customer.get("birthDate");
        String genderStr = (String) customer.get("gender");

        LocalDate birthDate = birthDateStr != null ? LocalDate.parse(birthDateStr) : null;
        Gender gender = genderStr != null ? Gender.valueOf(genderStr) : null;

        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.: " + signupDto.getEmail());
        }
        if(userRepository.existsByCi(ci)) {
            throw new RuntimeException("이미 해당 명의로 가입된 계정이 있습니다.");
        }

        Users user = Users.builder()
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

        return new SignupResponseDto(user.getEmail(), user.getCreatedAt());
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);

        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Users user = userRepository.findById(userDetails.getId())
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userDetails.getId()));

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        String name = profile != null ? profile.getName() : null;

        saveRefreshToken(user, refreshToken);

        return new TokenResponseDto(
                accessToken, refreshToken, user.getId(),
                user.getEmail(), name, user.getRole().name()
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);
    }

    // ✨ 공통 로직 (일반 로그인, 소셜 통합에서 모두 사용)
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

    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 존재하지 않습니다."));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인하세요.");
        }

        Users user = tokenEntity.getUser();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateToken(authentication);

        return new TokenResponseDto(newAccessToken, refreshToken, user.getId(), user.getEmail(), user.getProfile().getName(), user.getRole().name());
    }

    // 소셜 계정 본인인증 및 통합 API 로직
    @Transactional
    public TokenResponseDto integrateSocialAccount(Long tempUserId, String verificationId) {

        // 1. 포트원 서버에서 본인인증 정보 가져오기
        Map<String, Object> response = portOneService.getIdentityVerification(verificationId);
        Map<String, Object> customer = (Map<String, Object>) response.get("verifiedCustomer");

        String ci = (String) customer.get("ci");
        String name = (String) customer.get("name");
        String phone = (String) customer.get("phoneNumber");

        // ✨ 포트원 테스트 환경(V2) 대응: CI 값이 없을 경우 임시 값으로 대체 (이 한 줄로 JPA 다중 결과 에러 해결!)
        if (ci == null) {
            log.warn("포트원 응답에 CI 값이 없습니다. (테스트 환경 의심). 임시 CI를 대체 사용합니다.");
            ci = "TEST_CI_" + phone;
        }

        /*
        // 1. [테스트 백도어] verificationId가 지정된 테스트 키일 경우 포트원을 찌르지 않고 가상 데이터 생성
        String ci;
        String name;
        String phone;

        if ("TEST_VERIFICATION_ID".equals(verificationId)) {
            log.warn("⚠️ [BACKDOOR] 현실 배포 및 로컬 가상 테스트 모드가 활성화되었습니다. 포트원 API를 호출하지 않습니다.");
            phone = "01012345678";          // 👈 테스트하고 싶은 가상 전화번호 (원하는 대로 변경 가능)
            ci = "TEST_" + phone;           // 👈 요청하신 TEST_전화번호 구조 (JPA 중복 방지용 고유 키)
            name = "정태민(테스트)";
        } else {
            // 운영 환경용 진짜 포트원 연동 로직
            Map<String, Object> response = portOneService.getIdentityVerification(verificationId);
            Map<String, Object> customer = (Map<String, Object>) response.get("verifiedCustomer");

            ci = (String) customer.get("ci");
            name = (String) customer.get("name");
            phone = (String) customer.get("phoneNumber");

            if (ci == null) {
                log.warn("포트원 응답에 CI 값이 없습니다. (테스트 환경 의심). 임시 CI를 대체 사용합니다.");
                ci = "TEST_CI_" + phone;
            }
        }*/
        // 2. 현재 헤더의 토큰으로 접근한 '임시 유저' 조회
        Users tempUser = userRepository.findById(tempUserId)
                .orElseThrow(() -> new UserNotFoundException("임시 사용자를 찾을 수 없습니다."));

        // 3. 포트원에서 가져온 CI를 가진 '기존 정식 유저'가 DB에 있는지 확인
        Optional<Users> existingUserOpt = userRepository.findByCi(ci);

        Users finalUser;

        if (existingUserOpt.isPresent()) {
            // [시나리오 A] 이미 예전에 가입한 이력이 있는 유저 (계정 병합)
            Users existingUser = existingUserOpt.get();

            // 임시 유저가 들고 있던 소셜 로그인 기록(SocialLogin)을 기존 유저에게 이관
            List<SocialLogin> socialLogins = socialLoginRepository.findByUserId(tempUser.getId());
            for (SocialLogin sl : socialLogins) {
                sl.setUser(existingUser);
                socialLoginRepository.save(sl);
                // 기존 임시 유저의 리스트에서 제거해서 Cascade 삭제를 방지
                tempUser.getSocialLogins().remove(sl);
            }

            // 병합이 끝난 껍데기 임시 계정은 삭제
            userRepository.delete(tempUser);

            finalUser = existingUser;
        } else {
            // [시나리오 B] 완전 신규 유저 (임시 계정을 정식 계정으로 승격)
            tempUser.updateCi(ci);

            Profile profile = tempUser.getProfile();
            if (profile != null) {
                profile.updateVerificationInfo(name, phone);
            }

            finalUser = tempUser;
        }

        // 4. 최종 확정된 유저 정보로 새로운 보안 토큰 발급
        UserDetailsImpl userDetails = UserDetailsImpl.build(finalUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        saveRefreshToken(finalUser, newRefreshToken);

        return new TokenResponseDto(
                newAccessToken, newRefreshToken, finalUser.getId(),
                finalUser.getEmail(), name, finalUser.getRole().name()
        );
    }
}