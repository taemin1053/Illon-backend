package com.career.analytics.security;

import com.career.analytics.entity.RefreshToken;
import com.career.analytics.entity.Users;
import com.career.analytics.repository.RefreshTokenRepository;
import com.career.analytics.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Users user = userRepository.findById(oAuth2User.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + oAuth2User.getId()));

        // ✨ 핵심: CI가 없으면 신규 유저(true), 있으면 기존 유저(false)
        boolean isNewUser = (user.getCi() == null);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken.updateToken(refreshToken, expiresAt);
                            refreshTokenRepository.save(existingToken);
                        },
                        () -> {
                            RefreshToken newToken = RefreshToken.builder()
                                    .user(user)
                                    .token(refreshToken)
                                    .expiresAt(expiresAt)
                                    .build();
                            refreshTokenRepository.save(newToken);
                        }
                );

        log.info("소셜 로그인 성공: {}, 신규여부: {}", oAuth2User.getEmail(), isNewUser);

        // ✨ 드디어 isNewUser를 주소창 끝에 붙여줍니다!
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("isNewUser", isNewUser) // 👈 이제 이게 보일 겁니다.
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}