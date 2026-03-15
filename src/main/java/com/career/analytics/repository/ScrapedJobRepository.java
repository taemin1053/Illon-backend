package com.career.analytics.repository;

import com.career.analytics.entity.ScrapedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ScrapedJobRepository extends JpaRepository<ScrapedJob, Long> {
    List<ScrapedJob> findByUserId(Long userId);
}