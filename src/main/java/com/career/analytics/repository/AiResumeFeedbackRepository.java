package com.career.analytics.repository;

import com.career.analytics.entity.AiResumeFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiResumeFeedbackRepository extends JpaRepository<AiResumeFeedback, Long> {
}
