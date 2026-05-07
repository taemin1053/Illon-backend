package com.career.analytics.repository;

import com.career.analytics.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    // ✨ 낡은 잔재 2줄은 싹 다 지워버리고, 이력서(Resume) 기반의 새 메서드 장착!
    List<Certification> findByResumeIdOrderByIssueDateDesc(Long resumeId);

}