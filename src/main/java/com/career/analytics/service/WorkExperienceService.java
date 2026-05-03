package com.career.analytics.service;


import com.career.analytics.dto.WorkExperienceRequestDto;
import com.career.analytics.dto.WorkExperienceResponseDto;
import com.career.analytics.entity.Users;
import com.career.analytics.entity.WorkExperience;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.exception.WorkExperienceNotFoundException;
import com.career.analytics.repository.UserRepository;
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
    private final UserRepository userRepository;

    //내 경력 목록 조회
    @Transactional(readOnly = true)
    public List<WorkExperienceResponseDto> getMyWorkExperience() {
        Long userId = getCurrentUserId();

        return workExperienceRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(WorkExperienceResponseDto::new)
                .collect(Collectors.toList());

    }
    //  경력 추가
    @Transactional
    public WorkExperienceResponseDto createWorkExperience(WorkExperienceRequestDto workExperienceRequestDto) {
        Long userId = getCurrentUserId();

        Users user = userRepository.findById(userId).orElseThrow( ()-> new UserNotFoundException(userId));

        WorkExperience workExperience = WorkExperience.create(
                user,
                workExperienceRequestDto.getCompanyName(),
                workExperienceRequestDto.getDepartment(),
                workExperienceRequestDto.getRole(),
                workExperienceRequestDto.getStartDate(),
                workExperienceRequestDto.getEndDate(),
                workExperienceRequestDto.getIsCurrent(),
                workExperienceRequestDto.getDescription()
        );
        WorkExperience savedWorkExperience = workExperienceRepository.save(workExperience);
        return new WorkExperienceResponseDto(savedWorkExperience); // 세미콜론 추가!
    }

    //  경력 수정
    @Transactional
    public WorkExperienceResponseDto updateWorkExperience(Long workExperienceId, WorkExperienceRequestDto dto) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        // 본인 확인
        if (!workExperience.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 경력만 수정할 수 있습니다.");
        }

        // 엔티티 비즈니스 로직 활용 (더티 체킹)
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
    @Transactional
    public void deleteWorkExperience(Long workExperienceId) {
        Long userId = getCurrentUserId();

        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new WorkExperienceNotFoundException(workExperienceId));

        // 본인 확인
        if (!workExperience.getUser().getId().equals(userId)) {
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
