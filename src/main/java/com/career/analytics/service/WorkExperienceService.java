package com.career.analytics.service;

import com.career.analytics.dto.WorkExperienceRequestDto;
import com.career.analytics.dto.WorkExperienceResponseDto;
import com.career.analytics.entity.Resume;
import com.career.analytics.entity.WorkExperience;
import com.career.analytics.exception.UnauthorizedAccessException;
// import com.career.analytics.exception.ResumeNotFoundException; // 이 예외 클래스가 없다면 만들어야 합니다!
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
    // ✨ UserRepository 대신 ResumeRepository가 필요합니다.
    private final ResumeRepository resumeRepository;

    // ✨ 1. 특정 '이력서'의 경력 목록 조회 (파라미터로 resumeId를 받아야 함)
    @Transactional(readOnly = true)
    public List<WorkExperienceResponseDto> getWorkExperiencesByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        // 이력서 소유권 검증 (남의 이력서 조회 방지)
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. ID: " + resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        // ✨ 레포지토리 메서드 변경: UserId -> ResumeId
        return workExperienceRepository.findByResumeIdOrderByStartDateDesc(resumeId).stream()
                .map(WorkExperienceResponseDto::new)
                .collect(Collectors.toList());
    }

    // ✨ 2. 특정 '이력서'에 경력 추가
    @Transactional
    public WorkExperienceResponseDto createWorkExperience(Long resumeId, WorkExperienceRequestDto dto) {
        Long userId = getCurrentUserId();

        // 이력서 존재 여부 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. ID: " + resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 경력을 추가할 수 있습니다.");
        }

        WorkExperience workExperience = WorkExperience.create(
                resume, // ✨ 이제 진짜 찾아온 resume 객체를 넣습니다!
                dto.getCompanyName(),
                dto.getDepartment(),
                dto.getRole(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getIsCurrent(),
                dto.getDescription()
        );
        WorkExperience savedWorkExperience = workExperienceRepository.save(workExperience);
        return new WorkExperienceResponseDto(savedWorkExperience);
    }

    // ✨ 3. 경력 수정
    @Transactional
    public WorkExperienceResponseDto updateWorkExperience(Long workExperienceId, WorkExperienceRequestDto dto) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        // ✨ 본인 확인 로직 변경: user() 대신 resume().getUser()를 통해 타고 올라갑니다.
        if (!workExperience.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 경력만 수정할 수 있습니다.");
        }

        workExperience.update(
                dto.getCompanyName(),
                dto.getDepartment(),
                dto.getRole(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getIsCurrent(),
                dto.getDescription()
        );

        return new WorkExperienceResponseDto(workExperience);
    }

    // ✨ 4. 경력 삭제
    @Transactional
    public void deleteWorkExperience(Long workExperienceId) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        // ✨ 본인 확인 로직 변경
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