package com.career.analytics.service;

import com.career.analytics.dto.*;
import com.career.analytics.entity.*;
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.repository.UserRepository; // Users 레포지토리
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    /**
     * 1. 내 이력서 전체 목록 조회
     */
    public List<ResumeResponseDto> getMyResumes(Long userId) {
        return resumeRepository.findAllByUserId(userId).stream()
                .map(ResumeResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 2. 특정 이력서 상세 조회
     */
    public ResumeResponseDto getResumeDetail(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 접근 권한이 없는 이력서입니다."));
        return ResumeResponseDto.from(resume);
    }

    /**
     * 3. 새 이력서 생성 (모든 하위 데이터 포함)
     */
    @Transactional
    public Long createResume(Long userId, ResumeRequestDto requestDto) {
        // [1] DB에서 진짜 Users 객체 꺼내오기
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // [2] Resume 본체(부모) 조립
        Resume resume = Resume.builder()
                .user(user)
                .title(requestDto.getTitle())
                .isDefault(requestDto.getIsDefault() != null ? requestDto.getIsDefault() : false)
                .build();

        // [3] 하위 엔티티들(자식)을 부모에 끼워넣기 (null 체크 필수)
        // Resume 엔티티에 만들어둔 편의 메서드(addWorkExperience 등)를 사용합니다.

        if (requestDto.getWorkExperiences() != null) {
            requestDto.getWorkExperiences().forEach(dto ->
                    resume.addWorkExperience(dto.toEntity(resume)));
        }

        if (requestDto.getEducations() != null) {
            requestDto.getEducations().forEach(dto ->
                    resume.addEducation(dto.toEntity(resume)));
        }

        if (requestDto.getPortfolios() != null) {
            requestDto.getPortfolios().forEach(dto ->
                    resume.addPortfolio(dto.toEntity(resume)));
        }

        // Award와 Language는 편의 메서드가 없으므로 리스트에 직접 add (양방향 세팅)
        if (requestDto.getAwards() != null) {
            requestDto.getAwards().forEach(dto -> {
                Award award = dto.toEntity(resume);
                resume.getAwards().add(award);
            });
        }

        if (requestDto.getLanguages() != null) {
            requestDto.getLanguages().forEach(dto -> {
                Language language = dto.toEntity(resume);
                resume.getLanguages().add(language);
            });
        }
        // 자격증(Certification) 저장 로직
        if (requestDto.getCertifications() != null) {
            requestDto.getCertifications().forEach(dto -> {
                resume.addCertification(dto.toEntity(resume));
            });
        }

        // 자기소개서 (1:1 매핑)
        if (requestDto.getCoverLetter() != null) {
            resume.setCoverLetter(requestDto.getCoverLetter().toEntity(resume));
        }

        // [4] DB에 저장 (Cascade = ALL 마법 발동!)
        // 부모인 resume만 저장해도, 안에 들어있는 경력, 학력, 자소서가 싹 다 알아서 DB에 INSERT 됩니다.
        Resume savedResume = resumeRepository.save(resume);

        return savedResume.getId(); // 생성된 이력서 번호 반환
    }

    /**
     * 4. 이력서 삭제
     */
    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 접근 권한이 없는 이력서입니다."));

        // 부모를 지우면 orphanRemoval = true 설정 덕분에 자식 데이터도 DB에서 싹 지워집니다.
        resumeRepository.delete(resume);
    }
    /**
     * 5. 이력서 수정 (전체 갱신 버전)
     */
    @Transactional
    public void updateResume(Long resumeId, Long userId, ResumeRequestDto requestDto) {
        // [1] 기존 이력서 조회
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 접근 권한이 없는 이력서입니다."));

        // [2] 기본 정보 수정
        resume.updateTitle(requestDto.getTitle()); // 내부에서 문자열 검증까지 수행함
        if (requestDto.getIsDefault() != null && requestDto.getIsDefault()) {
            resume.setAsDefault(); //
        } else {
            resume.unsetDefault(); //
        }
        // [3] 자식 엔티티 전체 교체 (orphanRemoval = true 작동)

        // 경력 사항
        resume.getWorkExperiences().clear();
        if (requestDto.getWorkExperiences() != null) {
            requestDto.getWorkExperiences().forEach(dto ->
                    resume.addWorkExperience(dto.toEntity(resume)));
        }

        // 학력 사항
        resume.getEducations().clear();
        if (requestDto.getEducations() != null) {
            requestDto.getEducations().forEach(dto ->
                    resume.addEducation(dto.toEntity(resume)));
        }

        // 자격증
        resume.getCertifications().clear();
        if (requestDto.getCertifications() != null) {
            requestDto.getCertifications().forEach(dto ->
                    resume.addCertification(dto.toEntity(resume)));
        }

        // ✨ 추가: 수상 및 외국어 등 누락된 자식들도 clear 후 재설정
        resume.getAwards().clear();
        if (requestDto.getAwards() != null) {
            requestDto.getAwards().forEach(dto -> resume.getAwards().add(dto.toEntity(resume)));
        }

        resume.getLanguages().clear();
        if (requestDto.getLanguages() != null) {
            requestDto.getLanguages().forEach(dto -> resume.getLanguages().add(dto.toEntity(resume)));
        }

        // 자기소개서 (1:1)
        if (requestDto.getCoverLetter() != null) {
            resume.setCoverLetter(requestDto.getCoverLetter().toEntity(resume));
        }

        // 별도의 save() 없이 Transaction 종료 시 변경 감지(Dirty Checking)로 반영됨
    }
}