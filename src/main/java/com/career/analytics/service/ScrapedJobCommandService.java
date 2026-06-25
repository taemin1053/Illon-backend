package com.career.analytics.service;

import com.career.analytics.dto.ScrapStatusResponseDto;
import com.career.analytics.entity.ScrapedJob;
import com.career.analytics.entity.Users;
import com.career.analytics.repository.ScrapJobRepository;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapedJobCommandService {

    private final ScrapJobRepository scrapJobRepository;
    private final UserRepository userRepository;

    /**
     * 스크랩이 있으면 삭제하고 true 반환, 없으면 false 반환 (독립된 트랜잭션 실행)
     */
    @Transactional
    public boolean deleteIfExists(Long userId, String jobId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        Optional<ScrapedJob> existingScrap = scrapJobRepository.findByUserAndJobId(user, jobId);
        if (existingScrap.isPresent()) {
            scrapJobRepository.delete(existingScrap.get());
            return true;
        }
        return false;
    }

    /**
     * 파싱 로직을 포함하여 새로운 스크랩 정보 등록 (독립된 트랜잭션 실행)
     */
    @Transactional
    public void saveScrap(Long userId, String jobId, String deadlineStr, String createdAtStr) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        LocalDate deadline = parseDeadline(deadlineStr);
        LocalDateTime jobCreatedAt = parseJobCreatedAt(createdAtStr);

        ScrapedJob scrapedJob = ScrapedJob.createScrap(user, jobId, deadline, jobCreatedAt);
        scrapJobRepository.save(scrapedJob);
    }

    /**
     * 대시보드용 오늘의 스크랩 현황 집계 (읽기 전용 트랜잭션)
     */
    @Transactional(readOnly = true)
    public ScrapStatusResponseDto getScrapStatus(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        long totalScrapsCount = scrapJobRepository.countByUser(user);

        // 최근 3일 이내
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        long newJobsCount = scrapJobRepository.countByUserAndJobCreatedAtAfter(user, threeDaysAgo);

        // 마감 D-3 이내 (오늘 ~ 오늘+3일)
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);
        long expiringJobsCount = scrapJobRepository.countByUserAndDeadlineBetween(user, today, threeDaysLater);

        return new ScrapStatusResponseDto(newJobsCount, expiringJobsCount, totalScrapsCount);
    }

    // 날짜 및 시간 데이터 예외 방어 파싱 로직
    private LocalDate parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(deadlineStr.substring(0, 10));
        } catch (Exception e) {
            log.warn("⚠️ 마감일 날짜 파싱 실패 (상시채용 등). Raw: {}", deadlineStr);
            return null;
        }
    }

    private LocalDateTime parseJobCreatedAt(String createdAtStr) {
        if (createdAtStr == null || createdAtStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(createdAtStr.substring(0, 19));
        } catch (Exception e) {
            try {
                return LocalDate.parse(createdAtStr.substring(0, 10)).atStartOfDay();
            } catch (Exception ex) {
                log.warn("⚠️ 등록일 날짜 파싱 실패. Raw: {}", createdAtStr);
                return null;
            }
        }
    }
}
