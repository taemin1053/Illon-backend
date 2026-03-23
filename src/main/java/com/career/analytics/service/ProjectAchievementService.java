package com.career.analytics.service;


import com.career.analytics.dto.ProjectAchievementRequestDto;
import com.career.analytics.dto.ProjectAchievementResponseDto;
import com.career.analytics.entity.ProjectAchievement;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.ProjectAchievementNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.ProjectAchievementRepository;
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectAchievementService {
    private final ProjectAchievementRepository projectAchievementRepository;
    private final UserRepository userRepository;

    //User 프로젝트 목록 조회
    @Transactional(readOnly = true)
    public List<ProjectAchievementResponseDto> getMyProjects() {
        Long userId = getCurrentUserId();

        return projectAchievementRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(ProjectAchievementResponseDto::new)
                .collect(Collectors.toList());
    }
    //프로젝트 생성
    @Transactional
    public ProjectAchievementResponseDto createProject(ProjectAchievementRequestDto dto){
        Long userId = getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(userId));
        //builder 서비스에 직접 사용하기
        ProjectAchievement project = ProjectAchievement.builder()
                .user(user)
                .projectName(dto.getProjectName())
                .role(dto.getRole())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getDescription())
                .achievement(dto.getAchievement())
                .techStack(dto.getTechStack())
                .build();
        ProjectAchievement saved = projectAchievementRepository.save(project);

        return new ProjectAchievementResponseDto(saved);
    }

    //프로젝트 수정
    @Transactional
    public ProjectAchievementResponseDto updateProject(Long projectId, ProjectAchievementRequestDto dto){
        Long userId = getCurrentUserId();

        ProjectAchievement project = projectAchievementRepository.findById(projectId)
                .orElseThrow(() -> new ProjectAchievementNotFoundException(projectId));

        //본인 확인
        if(!project.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("본인의 프로젝트만 수정할 수 있습니다.");

        }
        //엔티티 로직 활용하기
        project.update(
                dto.getProjectName(),
                dto.getRole(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getDescription(),
                dto.getAchievement(),
                dto.getTechStack()
        );
        return new ProjectAchievementResponseDto(project);
    }
    //프로젝트 삭제하기
    public void deleteProject(Long projectId){
        Long userId = getCurrentUserId();

        ProjectAchievement project = projectAchievementRepository.findById(projectId)
                .orElseThrow(() -> new ProjectAchievementNotFoundException(projectId));

        if(!project.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("본인의 프로젝트만 삭제할 수 있습니다.");
        }
        projectAchievementRepository.delete(project);
    }
    //현재 로그인한 사용자 ID 가져오기
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
