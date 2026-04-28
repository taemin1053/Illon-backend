package com.career.analytics.repository;

import com.career.analytics.entity.CoverLetter;
import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
    List<CoverLetter> findAllByUserId(Long userId);
    Optional<CoverLetter> findByIdAndUser(Long id, Users user);
}
