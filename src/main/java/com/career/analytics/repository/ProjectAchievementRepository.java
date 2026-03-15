package com.career.analytics.repository;

import com.career.analytics.entity.ProjectAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectAchievementRepository extends JpaRepository<ProjectAchievement, Long> {
    List<ProjectAchievement> findByUserId(Long userId);

    List<ProjectAchievement> findByUserIdOrderByStartDateDesc(Long userId);
}
