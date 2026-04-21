package com.career.analytics.service;

import com.career.analytics.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    @Scheduled(cron = "0 0 4 * * *") // 초 분 시 일 월 요일
    public void cleanupExpiredTokens() {
        log.info("만료된 리프레시 토큰 청소 시작: {}", LocalDateTime.now());

        // 우리가 만든 expires_at 인덱스를 타고 미친 속도로 삭제합니다.
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        log.info("만료된 리프레시 토큰 청소 완료");
    }
}