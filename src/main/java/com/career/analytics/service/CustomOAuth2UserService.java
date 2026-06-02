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
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = null;
        String email = null;
        String name = null;
        String gender = null;

        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) throw new OAuth2AuthenticationException("네이버 사용자 정보가 없습니다.");
            providerId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
            gender = (String) response.get("gender");

        } else if ("kakao".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null) throw new OAuth2AuthenticationException("카카오 사용자 정보가 없습니다.");
            email = (String) kakaoAccount.get("email");
            String kakaoGender = (String) kakaoAccount.get("gender");
            if ("male".equalsIgnoreCase(kakaoGender)) gender = "M";
            else if ("female".equalsIgnoreCase(kakaoGender)) gender = "F";
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
            if (kakaoProfile != null) name = (String) kakaoProfile.get("nickname");

        } else if ("google".equals(registrationId)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 플랫폼: " + registrationId);
        }

        log.info("소셜 로그인 시도: [Provider: {}], [Email: {}]", registrationId, email);

        // ✨ 1차 방어막: 카카오 등에서 이메일 동의 안 했을 경우 원천 차단
        if (email == null) {
            throw new OAuth2AuthenticationException("소셜 계정의 이메일 정보가 없습니다. 동의 항목을 확인해주세요.");
        }

        // ✨ [가장 중요한 핵심 아키텍처 변경] 이메일로 찾기 전에, '소셜 식별자'로 먼저 찾아라!
        Optional<SocialLogin> linkedSocialLogin = socialLoginRepository.findByProviderAndProviderId(registrationId, providerId);
        Users user;

        if (linkedSocialLogin.isPresent()) {
            // 이전에 폰 인증으로 6번 계정에 합쳐진 네이버 로그인이라면, 이메일이 달라도 무조건 6번 유저를 소환!
            user = linkedSocialLogin.get().getUser();
            log.info(" [소셜 연동 계정 확인] 이메일에 구애받지 않고 통합된 정식 계정으로 로그인합니다. User ID: {}", user.getId());

        } else {
            // 연동 기록이 없을 때만 기존처럼 이메일 검색 진행
            Optional<Users> existingUserOpt = userRepository.findByEmail(email);

            if (existingUserOpt.isPresent()) {
                user = existingUserOpt.get();
                if (user.getCi() == null) {
                    log.info(" [임시 계정 발견] 본인인증(CI)이 완료되지 않은 임시 유저입니다. User ID: {}", user.getId());
                } else {
                    log.info(" [정식 회원 확인] 본인인증까지 완료된 기존 유저입니다. User ID: {}", user.getId());
                }
                linkSocialAccountIfNotExist(user, registrationId, providerId);

            } else {
                // 완전 신규: 임시 계정 생성
                log.info("🆕 [최초 로그인] 완전 신규 소셜 유저. 본인인증(CI) 전 임시 계정을 생성합니다. Email: {}", email);

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
        }

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