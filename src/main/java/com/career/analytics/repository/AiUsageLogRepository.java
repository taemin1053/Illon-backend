package com.career.analytics.repository;

import com.career.analytics.entity.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    List<AiUsageLog> findByUserId(Long userId);
}

