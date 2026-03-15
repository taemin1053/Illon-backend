package com.career.analytics.repository;


import com.career.analytics.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String Name);

}