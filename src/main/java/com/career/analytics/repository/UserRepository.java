package com.career.analytics.repository;

import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 💡 마이페이지용 프로필 정보를 한 번에 패치 조인하는 최적화 쿼리 (N+1 방지)
     */
    @Query("select u from Users u left join fetch u.profile where u.id = :id")
    Optional<Users> findByIdWithProfile(@Param("id") Long id);
}