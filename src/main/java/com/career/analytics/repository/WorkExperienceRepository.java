package com.career.analytics.repository;

import com.career.analytics.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface  WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByUserId(Long userId);
}

