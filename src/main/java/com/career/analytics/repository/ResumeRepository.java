package com.career.analytics.repository;

import com.career.analytics.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // 1. 특정 유저의 모든 이력서 목록 조회 (최신순 정렬 등은 Service에서 처리하거나 쿼리에 추가)
    List<Resume> findAllByUserId(Long userId);

    // 2. 특정 유저의 '기본 이력서(isDefault = true)' 단건 조회
    Optional<Resume> findByUserIdAndIsDefaultTrue(Long userId);

    // 3. 특정 유저의 특정 이력서 단건 조회 (보안상 userId도 같이 체크하는 것이 안전함)
    Optional<Resume> findByIdAndUserId(Long id, Long userId);
}