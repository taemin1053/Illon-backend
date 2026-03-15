package com.career.analytics.repository;

import com.career.analytics.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}

@Repository
interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    Optional<SocialLogin> findByProviderAndProviderId(String provider, String providerId);
    List<SocialLogin> findByUserId(Long userId);
}

@Repository
interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserId(Long userId);
}

@Repository
interface  WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByUserId(Long userId);
}

@Repository
interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findByUserId(Long userId);
}

@Repository
interface AwardRepository extends JpaRepository<Award, Long> {
    List<Award> findByUserId(Long userId);
}

@Repository
interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);
}

@Repository
interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String Name);

}

@Repository
interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
}

@Repository
interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String Name);
}

@Repository
interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByCompanyId(Long companyId);
}

@Repository
interface ScrapedJobRepository extends JpaRepository<ScrapedJob, Long> {
    List<ScrapedJob> findByUserId(Long userId);
}

@Repository
interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
    List<CoverLetter> findByUserIdAndDeletedAtIsNull(Long userId);
}

@Repository
interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    List<AiUsageLog> findByUserId(Long userId);
}

