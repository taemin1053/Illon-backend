package com.career.analytics.repository;

import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    //회원 가입용 중복확인
    Boolean existsByEmail(String email);

}
