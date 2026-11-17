package com.career.analytics.service;

import com.career.analytics.dto.AiAnalysisResponseDto;
import com.career.analytics.entity.Resume;
import com.career.analytics.entity.AiResumeFeedback;
import com.career.analytics.exception.AiServerException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.repository.AiResumeFeedbackRepository;
import com.career.analytics.repository.ResumeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAnalysisApplicationService {

    private final AiResumeAnalysisService aiResumeAnalysisService;
    private final ObjectMapper objectMapper;
    private final AiResumeFeedbackSaveService saveService; // 💡 트랜잭션 격리 전담 컴포넌트 주입
    private final ResumeRepository resumeRepository;

    /**
     * 💡 [Facade] 이력서 AI 분석 요청 및 결과를 DB에 영속화
     * 외부 API 대기 시간 동안 DB 커넥션 고갈을 방지하기 위해 이 메서드 레벨에는 @Transactional을 적용하지 않습니다.
     */
    public AiAnalysisResponseDto analyzeAndSave(Long resumeId) {
        log.info("📢 [AI 분석 프로세스 시작] 이력서 ID: {}", resumeId);

        // 0. DB에서 이력서 상세 정보 조회 및 텍스트 조합
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        String resumeText = buildResumeText(resume);

        // 1. AI 분석 수행 (트랜잭션 외부이므로 대기 중 DB 커넥션 점유 없음)
        AiAnalysisResponseDto responseDto = aiResumeAnalysisService.analyzeResume(resumeText);
        log.info("📢 [AI 분석 응답 완료] DTO 변환 성공. 이력서 ID: {}", resumeId);

        // 2. DTO 객체를 JSON String으로 직렬화
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            log.error("❌ AI 분석 결과 직렬화 실패. 이력서 ID: {}, 에러: {}", resumeId, e.getMessage());
            // 💡 [수정] RuntimeException 대신 커스텀 예외인 AiServerException으로 교체
            throw new AiServerException("AI 분석 결과 저장 데이터 변환에 실패했습니다.", e);
        }

        // 3. 트랜잭션 보장 범위 내에서만 안전하게 영속화 수행 (커넥션 점유 극최소화)
        saveService.saveFeedback(resumeId, jsonResult);

        return responseDto;
    }

    private String buildResumeText(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("이력서 제목: ").append(resume.getTitle()).append("\n\n");
        
        if (resume.getCoverLetter() != null) {
            sb.append("[자기소개서]\n")
              .append("내용: ").append(resume.getCoverLetter().getContent()).append("\n\n");
        }
        
        if (resume.getWorkExperiences() != null && !resume.getWorkExperiences().isEmpty()) {
            sb.append("[경력 사항]\n");
            resume.getWorkExperiences().forEach(we -> {
                sb.append("- 회사명: ").append(we.getCompanyName())
                  .append(", 부서: ").append(we.getDepartment())
                  .append(", 기간: ").append(we.getStartDate()).append(" ~ ").append(we.getEndDate() != null ? we.getEndDate() : "재직중")
                  .append("\n  상세 업무: ").append(we.getDescription()).append("\n");
            });
            sb.append("\n");
        }
        
        if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
            sb.append("[학력 사항]\n");
            resume.getEducations().forEach(edu -> {
                sb.append("- 학교명: ").append(edu.getSchoolName())
                  .append(", 전공: ").append(edu.getMajor())
                  .append(", 상태: ").append(edu.getStatus() != null ? edu.getStatus().name() : "")
                  .append(", 기간: ").append(edu.getStartDate()).append(" ~ ").append(edu.getEndDate() != null ? edu.getEndDate() : "")
                  .append("\n");
            });
            sb.append("\n");
        }

        if (resume.getAwards() != null && !resume.getAwards().isEmpty()) {
            sb.append("[수상 경력]\n");
            resume.getAwards().forEach(award -> {
                sb.append("- 수상명: ").append(award.getAwardName())
                  .append(", 기관: ").append(award.getOrganization())
                  .append(", 날짜: ").append(award.getAwardDate())
                  .append("\n  상세 내용: ").append(award.getDescription()).append("\n");
            });
            sb.append("\n");
        }

        if (resume.getLanguages() != null && !resume.getLanguages().isEmpty()) {
            sb.append("[외국어 능력]\n");
            resume.getLanguages().forEach(lang -> {
                sb.append("- 언어: ").append(lang.getLanguageName())
                  .append(", 시험명: ").append(lang.getTestName())
                  .append(", 점수: ").append(lang.getScore())
                  .append(", 취득일: ").append(lang.getTestDate())
                  .append("\n");
            });
            sb.append("\n");
        }

        if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
            sb.append("[자격증]\n");
            resume.getCertifications().forEach(cert -> {
                sb.append("- 자격증명: ").append(cert.getCertificationName())
                  .append(", 발급기관: ").append(cert.getIssuingOrganization())
                  .append(", 취득일: ").append(cert.getIssueDate())
                  .append("\n");
            });
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * 💡 DB 저장용 읽기/쓰기 트랜잭션 최소 격리 서비스
     * 내장 빈(Bean)으로 등록되어 AOP 프록시 트랜잭션이 완벽하게 가동됩니다.
     */
    @Service
    @RequiredArgsConstructor
    public static class AiResumeFeedbackSaveService {
        private final AiResumeFeedbackRepository aiResumeFeedbackRepository;

        @Transactional
        public void saveFeedback(Long resumeId, String jsonResult) {
            AiResumeFeedback feedback = AiResumeFeedback.builder()
                    .resumeId(resumeId)
                    .feedbackResult(jsonResult)
                    .build();

            aiResumeFeedbackRepository.save(feedback);
            log.info("💾 [DB 저장 성공] AI 피드백 영속화 완료. 이력서 ID: {}", resumeId);
        }
    }
}
