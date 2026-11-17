package com.career.analytics.service;

import com.career.analytics.dto.EducationRequestDto;
import com.career.analytics.dto.EducationResponseDto;
import com.career.analytics.entity.Education;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.EducationNotFoundException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.EducationRepository;
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ResumeRepository resumeRepository;

    @Transactional(readOnly = true)
    public List<EducationResponseDto> getMyEducationsByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if(!resume.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }
        return educationRepository.findByResumeIdOrderByStartDateDesc(resumeId).stream()
                .map(EducationResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EducationResponseDto createEducation(Long resumeId,EducationRequestDto dto) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new ResumeNotFoundException(resumeId));

        if(!resume.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("본인의 이력서에만 학력을 추가할 수 있습니다.");
        }

        Education education = Education.createEducation(
                resume,
                dto.getSchoolName(),
                dto.getMajor(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStatus(),
                // ✨ 빠져있던 부품(이수 과목/연구 내용)을 드디어 꽂아줍니다!
                dto.getDescription()
        );
        Education savedEducation = educationRepository.save(education);

        return new EducationResponseDto(savedEducation);
    }

    @Transactional
    public EducationResponseDto updateEducation(Long educationId, EducationRequestDto dto) {
        Long userId = getCurrentUserId();

        Education education = educationRepository.findById(educationId).orElseThrow(() -> new EducationNotFoundException(educationId.toString()));

        if(!education.getResume().getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("본인의 학력만 수정할 수 있습니다.");
        }

        education.update(
                dto.getSchoolName(),
                dto.getMajor(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStatus(),
                // ✨ 빠져있던 부품(이수 과목/연구 내용)을 드디어 꽂아줍니다!
                dto.getDescription()
        );
        return new EducationResponseDto(education);
    }

    @Transactional
    public void deleteEducation(Long educationId) {
        Long userId = getCurrentUserId();

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException(educationId.toString()));

        if (!education.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 학력만 삭제할 수 있습니다.");
        }

        educationRepository.delete(education);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}