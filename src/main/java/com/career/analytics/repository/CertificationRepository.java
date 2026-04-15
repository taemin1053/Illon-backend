package com.career.analytics.repository;

import com.career.analytics.entity.Certification;
import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByUserIdOrderByIssueDateDesc(Long userId);

    Long user(Users user);
}
