package com.career.analytics.repository;

import com.career.analytics.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    // 특정 유저의 이력서 목록을 조회할 때 사용할 메서드
    List<Resume> findByUserId(Long userId);
}