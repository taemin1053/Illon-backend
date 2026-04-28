package com.career.analytics.repository;

import com.career.analytics.entity.Award;
import com.career.analytics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {
    List<Award> findByUserId(Long userId);

    List<Award> findByUserIdOrderByAwardDateDesc(Long userId);


}



