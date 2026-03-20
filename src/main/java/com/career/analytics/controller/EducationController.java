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
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Education", description = "학력 정보 관리 API")
public class EducationController {

    private final EducationService educationService;

    //학력 목록 조회
    //GET /api/educations
    @Operation(summary = "내 학력 목록 조회", description = "로그인한 사용자의 전체 학력 목록을 가져옵니다.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EducationResponseDto>> getMyEducations() {
        List<EducationResponseDto> educations = educationService.getMyEducations();
        return ResponseEntity.ok(educations);
    }

    // 학력 추가 (POST)
    @Operation(summary = "학력 추가", description = "로그인한 사용자의 새로운 학력 정보를 등록합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EducationResponseDto> createEducation(@Valid @RequestBody EducationRequestDto educationRequestDto) {
        // 성공하는 '해피 경로(Happy Path)'만 남깁니다. 예외는 GlobalExceptionHandler가 다 잡습니다.
        EducationResponseDto education = educationService.createEducation(educationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(education);
    }

    // 학력 수정 (PUT)
    @Operation(summary = "학력 수정", description = "특정 학력(educationId)의 정보를 수정합니다.")
    @PutMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EducationResponseDto> updateEducation(
            @PathVariable("educationId") Long educationId,
            @Valid @RequestBody EducationRequestDto educationRequestDto) {

        EducationResponseDto education = educationService.updateEducation(educationId, educationRequestDto);
        return ResponseEntity.ok(education);
    }

    // 학력 삭제 (DELETE)
    @Operation(summary = "학력 삭제", description = "특정 학력(educationId)의 정보를 삭제합니다.")
    @DeleteMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteEducation(@PathVariable("educationId") Long educationId) {
        educationService.deleteEducation(educationId);
        return ResponseEntity.ok("Deleted Successfully");
    }

}
