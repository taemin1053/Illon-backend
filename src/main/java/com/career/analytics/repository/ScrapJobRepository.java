package com.career.analytics.repository;

import com.career.analytics.entity.ScrapedJob;
import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapJobRepository extends JpaRepository<ScrapedJob, Long> {

    // 토글용 단건 조회 (Users 타입 반영)
    Optional<ScrapedJob> findByUserAndJobId(Users user, String jobId);

    // 마이페이지용 최신순 전체 조회 (Users 타입 반영)
    List<ScrapedJob> findByUserOrderByCreatedAtDesc(Users user);
}