package com.career.analytics.service;

import com.career.analytics.dto.*;
import com.career.analytics.entity.*;
import com.career.analytics.exception.ResumeNotFoundException; // ✨ 예외 추가
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.repository.UserRepository;
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
    private final S3PresignedUrlService s3PresignedUrlService;

    public List<ResumeResponseDto> getMyResumes(Long userId) {
        return resumeRepository.findAllByUserId(userId).stream()
                .map(resume -> {
                    ResumeResponseDto responseDto = ResumeResponseDto.from(resume);
                    convertPortfolioUrls(responseDto);
                    return responseDto;
                })
                .collect(Collectors.toList());
    }

    public ResumeResponseDto getResumeDetail(Long resumeId, Long userId) {
        // ✨ IllegalArgumentException 대신 ResumeNotFoundException 사용 및 오타 방지
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        ResumeResponseDto responseDto = ResumeResponseDto.from(resume);
        convertPortfolioUrls(responseDto);
        return responseDto;
    }

    private void convertPortfolioUrls(ResumeResponseDto responseDto) {
        if (responseDto.getPortfolios() == null || responseDto.getPortfolios().isEmpty()) {
            return;
        }

        responseDto.getPortfolios().forEach(portfolio -> {
            String originalUrl = portfolio.getUrl();
            if (originalUrl != null && !originalUrl.isBlank()) {
                String objectKey = s3PresignedUrlService.extractKeyFromUrl(originalUrl);
                String presignedUrl = s3PresignedUrlService.generatePresignedUrl(objectKey);
                portfolio.setUrl(presignedUrl);
            }
        });
    }

    @Transactional
    public Long createResume(Long userId, ResumeRequestDto requestDto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Resume resume = Resume.builder()
                .user(user)
                .title(requestDto.getTitle())
                .isDefault(requestDto.getIsDefault() != null ? requestDto.getIsDefault() : false)
                .build();

        // [자식 엔티티 조립] - 모든 항목을 편의 메서드(addXXX)로 통일하여 일관성 확보
        if (requestDto.getWorkExperiences() != null) {
            requestDto.getWorkExperiences().forEach(dto -> resume.addWorkExperience(dto.toEntity(resume)));
        }

        if (requestDto.getEducations() != null) {
            requestDto.getEducations().forEach(dto -> resume.addEducation(dto.toEntity(resume)));
        }

        if (requestDto.getPortfolios() != null) {
            requestDto.getPortfolios().forEach(dto -> resume.addPortfolio(dto.toEntity(resume)));
        }

        // ✨ 수정 포인트: 리스트에 직접 add 대신 엔티티 편의 메서드 사용
        if (requestDto.getAwards() != null) {
            requestDto.getAwards().forEach(dto -> resume.addAward(dto.toEntity(resume)));
        }

        if (requestDto.getLanguages() != null) {
            requestDto.getLanguages().forEach(dto -> resume.addLanguage(dto.toEntity(resume)));
        }

        if (requestDto.getCertifications() != null) {
            requestDto.getCertifications().forEach(dto -> resume.addCertification(dto.toEntity(resume)));
        }

        if (requestDto.getCoverLetter() != null) {
            resume.setCoverLetter(requestDto.getCoverLetter().toEntity(resume));
        }

        return resumeRepository.save(resume).getId();
    }

    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        resumeRepository.delete(resume);
    }

    @Transactional
    public void updateResume(Long resumeId, Long userId, ResumeRequestDto requestDto) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        resume.updateTitle(requestDto.getTitle());
        if (requestDto.getIsDefault() != null && requestDto.getIsDefault()) {
            resume.setAsDefault();
        } else {
            resume.unsetDefault();
        }

        // 자식 엔티티 교체 시에도 편의 메서드로 일관성 유지
        resume.getWorkExperiences().clear();
        if (requestDto.getWorkExperiences() != null) {
            requestDto.getWorkExperiences().forEach(dto -> resume.addWorkExperience(dto.toEntity(resume)));
        }

        resume.getEducations().clear();
        if (requestDto.getEducations() != null) {
            requestDto.getEducations().forEach(dto -> resume.addEducation(dto.toEntity(resume)));
        }

        resume.getCertifications().clear();
        if (requestDto.getCertifications() != null) {
            requestDto.getCertifications().forEach(dto -> resume.addCertification(dto.toEntity(resume)));
        }

        // ✨ 수정 포인트: 수상 및 외국어 연관관계 로직 통일
        resume.getAwards().clear();
        if (requestDto.getAwards() != null) {
            requestDto.getAwards().forEach(dto -> resume.addAward(dto.toEntity(resume)));
        }

        resume.getLanguages().clear();
        if (requestDto.getLanguages() != null) {
            requestDto.getLanguages().forEach(dto -> resume.addLanguage(dto.toEntity(resume)));
        }

        if (requestDto.getCoverLetter() != null) {
            resume.setCoverLetter(requestDto.getCoverLetter().toEntity(resume));
        }
    }
}