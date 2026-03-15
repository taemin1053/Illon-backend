package com.career.analytics.repository;

import com.career.analytics.entity.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    Optional<SocialLogin> findByProviderAndProviderId(String provider, String providerId);
    List<SocialLogin> findByUserId(Long userId);
}