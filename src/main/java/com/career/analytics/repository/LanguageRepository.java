package com.career.analytics.repository;

import com.career.analytics.entity.Language;
import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findByUserId(Long userId);

    List<Language> user(Users user);

    List<Language> findByUserIdOrderByTestDateDesc(Long userId);
}
