package com.career.analytics.controller;

import com.career.analytics.dto.WorkExperienceRequestDto;
import com.career.analytics.dto.WorkExperienceResponseDto;
import com.career.analytics.service.WorkExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//  경로 변경: 이제 이력서(resumes) 하위 자원으로 관리합니다.
@RequestMapping("/api/resumes/{resumeId}/work-experiences")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "Work Experience", description = "이력서 - 경력 정보 관리 API")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;

    @Operation(summary = "특정 이력서의 경력 목록 조회", description = "해당 이력서에 등록된 전체 경력 목록을 조회합니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WorkExperienceResponseDto>> getWorkExperiences(
            @PathVariable("resumeId") Long resumeId) { // ✨ 서비스에 전달할 resumeId 확보
        List<WorkExperienceResponseDto> workExperiences = workExperienceService.getWorkExperiencesByResume(resumeId);
        return ResponseEntity.ok(workExperiences);
    }

    @Operation(summary = "경력 추가", description = "특정 이력서에 새로운 경력 정보를 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkExperienceResponseDto> createWorkExperience(
            @PathVariable("resumeId") Long resumeId, // ✨ 어느 이력서에 넣을지 경로에서 받아옴
            @Valid @RequestBody WorkExperienceRequestDto dto) {
        WorkExperienceResponseDto workExperience = workExperienceService.createWorkExperience(resumeId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(workExperience);
    }

    //  수정/삭제는 이미 고유 ID(workExperienceId)가 있으므로 경로를 분리해서 생각할 수도 있지만,
    // 일관성을 위해 부모 경로를 유지하는 방식을 주로 씁니다.

    @Operation(summary = "경력 수정", description = "특정 경력의 정보를 수정합니다.")
    @PutMapping("/{workExperienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkExperienceResponseDto> updateWorkExperience(
            @PathVariable("resumeId") Long resumeId, // 경로는 맞추되 검증은 서비스에서 수행
            @PathVariable("workExperienceId") Long workExperienceId,
            @Valid @RequestBody WorkExperienceRequestDto dto) {
        WorkExperienceResponseDto workExperience = workExperienceService.updateWorkExperience(workExperienceId, dto);
        return ResponseEntity.ok(workExperience);
    }

    @Operation(summary = "경력 삭제", description = "특정 경력 정보를 삭제합니다.")
    @DeleteMapping("/{workExperienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteWorkExperience(
            @PathVariable("resumeId") Long resumeId,
            @PathVariable("workExperienceId") Long workExperienceId) {
        workExperienceService.deleteWorkExperience(workExperienceId);
        return ResponseEntity.noContent().build();
    }
}