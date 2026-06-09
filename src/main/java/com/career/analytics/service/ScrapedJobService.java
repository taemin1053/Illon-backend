package com.career.analytics.service;

import com.career.analytics.entity.ScrapedJob;
import com.career.analytics.entity.Users;
import com.career.analytics.repository.ScrapJobRepository;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScrapedJobService {

    private final ScrapJobRepository scrapJobRepository;
    private final UserRepository userRepository; // 💡 실제 Users 엔티티 조회를 위해 UserRepository 추가

    /**
     * 채용 공고 스크랩 토글 (등록 및 삭제)
     * 
     * @param userId 현재 로그인한 유저의 고유 식별자(PK)
     * @param jobId  채용 공고 식별자
     * @return 스크랩 상태 결과값 ("SCRAP_CREATED" 또는 "SCRAP_DELETED")
     */
    @Transactional
    public String toggleScrap(Long userId, String jobId) {
        // 1. 유저 ID로 실제 DB에서 Users 엔티티 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        // 2. 이미 스크랩한 공고인지 확인
        Optional<ScrapedJob> existingScrap = scrapJobRepository.findByUserAndJobId(user, jobId);

        if (existingScrap.isPresent()) {
            // 3. 이미 존재한다면 삭제 (스크랩 취소)
            scrapJobRepository.delete(existingScrap.get());
            return "SCRAP_DELETED";
        } else {
            // 4. 존재하지 않는다면 정적 팩토리 메서드를 통해 새로 생성 후 저장 (스크랩 등록)
            ScrapedJob scrapedJob = ScrapedJob.createScrap(user, jobId);
            scrapJobRepository.save(scrapedJob);
            return "SCRAP_CREATED";
        }
    }
}
