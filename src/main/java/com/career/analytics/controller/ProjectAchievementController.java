package com.career.analytics.controller;

import com.career.analytics.dto.ProjectAchievementRequestDto;
import com.career.analytics.dto.ProjectAchievementResponseDto;
import com.career.analytics.service.ProjectAchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Achievement", description = "이력서 - 과거 프로젝트 성과 API")
public class ProjectAchievementController {
    private final ProjectAchievementService projectAchievementService;

    //프로젝트 목록조회
    @Operation(summary = "내 프로젝트 성과 조회 ", description = "로그인한 사용자의 전체 프로젝트 조회")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectAchievementResponseDto>> getMYProject() {
        List<ProjectAchievementResponseDto> projects = projectAchievementService.getMyProjects();
        return ResponseEntity.ok(projects);
    }
    //프로젝트 생성
    @Operation(summary = "프로젝트 성과추가", description = "새로운 프로젝트를 등록 추가")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectAchievementResponseDto> createProject(@Valid @RequestBody ProjectAchievementRequestDto dto) {
        ProjectAchievementResponseDto project = projectAchievementService.createProject(dto);
        return ResponseEntity.ok(project);
    }
    //프로젝트 수정
    @Operation(summary = "프로젝트 성과 수정 ", description = "특정 프로젝트를 수정합니다.")
    @PutMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectAchievementResponseDto> updateProject(@PathVariable("projectId") Long projectId,
                                                                       @Valid @RequestBody ProjectAchievementRequestDto dto) {
        ProjectAchievementResponseDto project = projectAchievementService.updateProject(projectId, dto);
        return ResponseEntity.ok(project);
    }
    //프로젝트 삭제
    @Operation(summary = "프로젝트 성과 삭제", description = "특정 프로그램을 삭제")
    @DeleteMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProject(@PathVariable("projectId") Long projectId) {
        projectAchievementService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

}
