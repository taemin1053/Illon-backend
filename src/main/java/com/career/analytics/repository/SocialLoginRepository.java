package com.career.analytics.repository;

import com.career.analytics.entity.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

    // 특정 소셜 플랫폼(provider)의 고유 식별자(providerId)로 존재 여부 확인
    boolean existsByProviderAndProviderId(String provider, String providerId);

    // ✨ 소셜 계정 통합 시, 임시 유저가 가진 소셜 로그인 기록을 찾아오기 위해 추가!
    List<SocialLogin> findByUserId(Long userId);
    Optional<SocialLogin> findByProviderAndProviderId(String provider, String providerId);
}