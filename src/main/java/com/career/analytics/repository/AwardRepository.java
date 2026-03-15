package com.career.analytics.repository;

import com.career.analytics.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface AwardRepository extends JpaRepository<Award, Long> {
    List<Award> findByUserId(Long userId);
}



