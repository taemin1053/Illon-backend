package com.career.analytics.service;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.dto.ScrapStatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapedJobService {

    private final ScrapedJobCommandService commandService; // 💡 DB 트랜잭션 전담 서비스 주입
    private final JobBffService jobBffService;             // 💡 외부 API 전담 서비스 주입

    /**
     * 채용 공고 스크랩 토글 (네트워크 I/O를 트랜잭션 바깥으로 완벽 격리)
     */
    public String toggleScrap(Long userId, String jobId) {
        log.info("🚪 [Facade] 스크랩 토글 비즈니스 흐름 시작 - 유저 ID: {}, 공고 ID: {}", userId, jobId);

        // 1단계 & 2단계: 기존 스크랩이 존재하면 삭제 후 즉시 커밋 및 트랜잭션 종료
        boolean isDeleted = commandService.deleteIfExists(userId, jobId);
        if (isDeleted) {
            log.info("🗑️ [Facade] 기존 스크랩 감지 및 삭제 완료");
            return "SCRAP_DELETED";
        }

        // 3단계: 존재하지 않는 경우 외부 API 호출 (🚨 DB Connection을 잡고 있지 않은 상태)
        log.info("🌐 [Facade] 외부 API 호출 진행 (트랜잭션 외부) - 공고 ID: {}", jobId);
        JobDetailResponseDto jobDetail = jobBffService.getJobDetail(jobId);

        // 4단계: 받아온 정보를 기반으로 새로운 트랜잭션을 획득하여 저장 진행
        log.info("💾 [Facade] 신규 스크랩 정보 영속화 진행");
        commandService.saveScrap(userId, jobId, jobDetail.deadline(), jobDetail.createdAt());

        return "SCRAP_CREATED";
    }

    /**
     * 대시보드용 오늘의 스크랩 현황 집계 위임
     */
    public ScrapStatusResponseDto getScrapStatus(Long userId) {
        return commandService.getScrapStatus(userId);
    }
}
