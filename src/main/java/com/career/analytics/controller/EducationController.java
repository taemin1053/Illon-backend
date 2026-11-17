package com.career.analytics.controller;

import com.career.analytics.dto.EducationRequestDto;
import com.career.analytics.dto.EducationResponseDto;
import com.career.analytics.service.EducationService;
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
// 1. URL 경로 변경: 이력서 하위 자원으로 구조화
@RequestMapping("/api/resumes/{resumeId}/educations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Education", description = "학력 정보 관리 API")
public class EducationController {

    private final EducationService educationService;

    @Operation(summary = "특정 이력서의 학력 목록 조회", description = "해당 이력서에 등록된 전체 학력 목록을 가져옵니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EducationResponseDto>> getEducations(
            @PathVariable("resumeId") Long resumeId) { // 경로에서 resumeId 확보
        // 서비스 메서드명 변경 반영
        List<EducationResponseDto> educations = educationService.getMyEducationsByResume(resumeId);
        return ResponseEntity.ok(educations);
    }

    @Operation(summary = "학력 추가", description = "특정 이력서에 새로운 학력 정보를 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EducationResponseDto> createEducation(
            @PathVariable("resumeId") Long resumeId, // ✨ 경로에서 resumeId 확보
            @Valid @RequestBody EducationRequestDto educationRequestDto) {

        // ✨ 서비스에 resumeId 전달
        EducationResponseDto education = educationService.createEducation(resumeId, educationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(education);
    }

    @Operation(summary = "학력 수정", description = "특정 학력(educationId)의 정보를 수정합니다.")
    @PutMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EducationResponseDto> updateEducation(
            @PathVariable("resumeId") Long resumeId, // 일관성을 위해 유지
            @PathVariable("educationId") Long educationId,
            @Valid @RequestBody EducationRequestDto educationRequestDto) {

        EducationResponseDto education = educationService.updateEducation(educationId, educationRequestDto);
        return ResponseEntity.ok(education);
    }

    @Operation(summary = "학력 삭제", description = "특정 학력(educationId)의 정보를 삭제합니다.")
    @DeleteMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEducation( //  String 반환 대신 Void로 변경
                                                 @PathVariable("resumeId") Long resumeId,
                                                 @PathVariable("educationId") Long educationId) {

        educationService.deleteEducation(educationId);
        // 데이터 삭제 성공 시 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}