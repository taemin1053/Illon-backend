package com.career.analytics.service;

import com.career.analytics.dto.WorkExperienceRequestDto;
import com.career.analytics.dto.WorkExperienceResponseDto;
import com.career.analytics.entity.Resume;
import com.career.analytics.entity.WorkExperience;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.WorkExperienceNotFoundException;
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.repository.WorkExperienceRepository;
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
public class WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final ResumeRepository resumeRepository;

    @Transactional(readOnly = true)
    public List<WorkExperienceResponseDto> getWorkExperiencesByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. ID: " + resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        return workExperienceRepository.findByResumeIdOrderByStartDateDesc(resumeId).stream()
                .map(WorkExperienceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkExperienceResponseDto createWorkExperience(Long resumeId, WorkExperienceRequestDto dto) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. ID: " + resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 경력을 추가할 수 있습니다.");
        }

        WorkExperience workExperience = WorkExperience.create(
                resume,
                dto.getCompanyName(),
                dto.getDepartment(),
                dto.getRole(),
                // ✨ 빠져있던 두 가지 부품(재직형태, 직책)을 정확한 위치에 꽂아 넣습니다!
                dto.getEmploymentType(),
                dto.getPosition(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getIsCurrent(),
                dto.getDescription()
        );
        WorkExperience savedWorkExperience = workExperienceRepository.save(workExperience);
        return new WorkExperienceResponseDto(savedWorkExperience);
    }

    @Transactional
    public WorkExperienceResponseDto updateWorkExperience(Long workExperienceId, WorkExperienceRequestDto dto) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        if (!workExperience.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 경력만 수정할 수 있습니다.");
        }

        workExperience.update(
                dto.getCompanyName(),
                dto.getDepartment(),
                dto.getRole(),
                // ✨ 빠져있던 두 가지 부품(재직형태, 직책)을 정확한 위치에 꽂아 넣습니다!
                dto.getEmploymentType(),
                dto.getPosition(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getIsCurrent(),
                dto.getDescription()
        );

        return new WorkExperienceResponseDto(workExperience);
    }

    @Transactional
    public void deleteWorkExperience(Long workExperienceId) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        if (!workExperience.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 경력만 삭제할 수 있습니다.");
        }

        workExperienceRepository.delete(workExperience);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}