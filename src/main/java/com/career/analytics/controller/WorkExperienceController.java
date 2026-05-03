package com.career.analytics.controller;


import com.career.analytics.dto.WorkExperienceRequestDto;
import com.career.analytics.dto.WorkExperienceResponseDto;
import com.career.analytics.entity.WorkExperience;
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
@RequestMapping("api/work-experiences")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "Work Experience", description = "이력서 - 경력 정보 관리 API")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;

    @Operation(summary = "내 경력 목록 조회 ", description = "로그인한 사용자의 전체 경력 목록 조회")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WorkExperienceResponseDto>> getMyWorkExperience() {
        List<WorkExperienceResponseDto> workExperiences = workExperienceService.getMyWorkExperience();
        return ResponseEntity.ok(workExperiences);
    }
    @Operation(summary = "경력 추가", description = "로그인한 사용자의 새로운 경력 정보를 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkExperienceResponseDto> createWorkExperience(
            @Valid @RequestBody WorkExperienceRequestDto dto) {
        WorkExperienceResponseDto workExperience = workExperienceService.createWorkExperience(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(workExperience);
    }
    @Operation(summary = "경력 수정", description = "특정 경력(workExperienceId로 확인)의 정보를 수정")
    @PutMapping("/{workExperienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkExperienceResponseDto> updateWorkExperience(
            @PathVariable("workExperienceId") Long workExperienceId,
            @Valid  @RequestBody WorkExperienceRequestDto dto){
        WorkExperienceResponseDto workExperience = workExperienceService.updateWorkExperience(workExperienceId, dto);
        return ResponseEntity.ok(workExperience);
    }

    @Operation(summary = "경력 삭제", description = "특정 경력(workExperienceId)의 정보를 삭제합니다.")
    @DeleteMapping("/{workExperienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteWorkExperience(
            @PathVariable("workExperienceId") Long workExperienceId) {
        workExperienceService.deleteWorkExperience(workExperienceId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환 (삭제 성공의 정석)
    }
}
