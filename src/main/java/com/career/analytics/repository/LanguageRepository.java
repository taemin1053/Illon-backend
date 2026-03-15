package com.career.analytics.repository;

import com.career.analytics.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findByUserId(Long userId);
}
