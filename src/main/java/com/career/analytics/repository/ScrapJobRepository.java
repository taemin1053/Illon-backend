package com.career.analytics.repository;

import com.career.analytics.entity.ScrapedJob;
import com.career.analytics.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapJobRepository extends JpaRepository<ScrapedJob, Long> {

    // 토글용 단건 조회 (Users 타입 반영)
    Optional<ScrapedJob> findByUserAndJobId(Users user, String jobId);

    // 마이페이지용 최신순 전체 조회 (Users 타입 반영)
    List<ScrapedJob> findByUserOrderByCreatedAtDesc(Users user);

    // 유저별 스크랩 공고 페이징 조회
    Page<ScrapedJob> findByUser(Users user, Pageable pageable);

    // 💡 전체 스크랩 개수 카운트
    long countByUser(Users user);

    // 💡 최근 3일 이내에 등록된 공고 스크랩 개수 카운트
    long countByUserAndJobCreatedAtAfter(Users user, LocalDateTime dateTime);

    // 💡 마감일이 오늘 기준 D-3 이내(오늘 ~ 오늘+3일)인 공고 스크랩 개수 카운트
    long countByUserAndDeadlineBetween(Users user, LocalDate startDate, LocalDate endDate);
}