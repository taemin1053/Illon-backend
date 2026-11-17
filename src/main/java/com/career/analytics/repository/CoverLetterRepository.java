package com.career.analytics.repository;

import com.career.analytics.entity.CoverLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // ✨ List 대신 Optional 임포트!

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    // ✨ 이력서 1개당 자소서는 1개이므로 Optional로 반환합니다!
    Optional<CoverLetter> findByResumeId(Long resumeId);

}