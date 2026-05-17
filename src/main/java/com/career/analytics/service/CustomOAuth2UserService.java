package com.career.analytics.service;

import com.career.analytics.entity.*;
import com.career.analytics.repository.ProfileRepository;
import com.career.analytics.repository.SocialLoginRepository;
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SocialLoginRepository socialLoginRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 소셜 서버에서 유저 정보 긁어오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 제공자 식별 (naver, kakao, google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 파싱할 데이터 변수 선언
        String providerId = null;
        String email = null;
        String name = null;
        String gender = null;

        // 2. 소셜 제공자(Provider)별 JSON 응답 파싱 로직 분기
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) {
                throw new OAuth2AuthenticationException("네이버 사용자 정보(response)를 찾을 수 없습니다.");
            }
            providerId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
            gender = (String) response.get("gender");

        } else if ("kakao".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            if (kakaoAccount == null) {
                throw new OAuth2AuthenticationException("카카오 사용자 정보(kakao_account)를 찾을 수 없습니다.");
            }

            email = (String) kakaoAccount.get("email");
            String kakaoGender = (String) kakaoAccount.get("gender");

            if ("male".equalsIgnoreCase(kakaoGender)) {
                gender = "M";
            } else if ("female".equalsIgnoreCase(kakaoGender)) {
                gender = "F";
            }

            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
            if (kakaoProfile != null) {
                name = (String) kakaoProfile.get("nickname");
            }

        } else if ("google".equals(registrationId)) { // ✨ [구글 로그인 파싱 분기 전격 추가]
            // 구글(OIDC 표준)의 고유 식별자 PK 키는 'sub' 입니다.
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            // 구글 기본 scope(profile, email)에서는 성별을 제공하지 않으므로 gender = null 유지

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        log.info("소셜 로그인 시도: [Provider: {}], [Email: {}]", registrationId, email);

        // 3. 이메일로 1차 조회 및 CI 유무에 따른 명확한 판단
        Optional<Users> existingUserOpt = userRepository.findByEmail(email);
        Users user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();

            if (user.getCi() == null) {
                log.info("⚠️ [임시 계정 발견] 이메일은 존재하나 본인인증(CI)이 완료되지 않은 임시 유저입니다. User ID: {}", user.getId());
            } else {
                log.info("✅ [정식 회원 확인] 이미 본인인증까지 완료된 기존 유저입니다. User ID: {}", user.getId());
            }

            linkSocialAccountIfNotExist(user, registrationId, providerId);
        } else {
            // 4. 완전 최초 로그인: 신규 유저지만 CI가 없는 '임시 계정' 생성
            log.info("🆕 [최초 로그인] 완전 신규 소셜 유저입니다. 본인인증(CI) 전 임시 계정을 생성합니다. Email: {}", email);

            user = Users.builder()
                    .email(email)
                    .password(UUID.randomUUID().toString())
                    .role(Role.USER)
                    .build();
            userRepository.save(user);

            Profile profile = Profile.builder()
                    .user(user)
                    .name(name)
                    .gender("M".equals(gender) ? Gender.MALE : ("F".equals(gender) ? Gender.FEMALE : null))
                    .build();
            profileRepository.save(profile);

            user.setProfile(profile);
            linkSocialAccountIfNotExist(user, registrationId, providerId);
        }

        // 5. 시큐리티 세션에 담을 객체 반환
        return new UserDetailsImpl(user, attributes);
    }

    private void linkSocialAccountIfNotExist(Users user, String provider, String providerId) {
        boolean isLinked = socialLoginRepository.existsByProviderAndProviderId(provider, providerId);
        if (!isLinked) {
            SocialLogin socialLogin = SocialLogin.builder()
                    .user(user)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            socialLoginRepository.save(socialLogin);
            log.info("소셜 연동 완료: [Provider: {}], [User ID: {}]", provider, user.getId());
        }
    }
}