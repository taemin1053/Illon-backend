package com.career.analytics.repository;

import com.career.analytics.entity.CoverLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
    List<CoverLetter> findByUserIdAndDeletedAtIsNull(Long userId);
}

