package com.career.analytics.repository;

import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    //회원 가입용 중복확인
    Boolean existsByEmail(String email);

    // 🔥 포트원 본인인증 CI 기반 조회 및 중복 검증용 추가
    Optional<Users> findByCi(String ci);
    Boolean existsByCi(String ci);
}