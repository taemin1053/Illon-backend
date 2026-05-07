package com.career.analytics.controller;

import com.career.analytics.dto.ResumeRequestDto;
import com.career.analytics.dto.ResumeResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서 API", description = "이력서 생성, 상세 조회, 목록 조회, 삭제 기능")
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "내 이력서 전체 목록 조회", description = "현재 로그인한 유저의 모든 이력서를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ResumeResponseDto>> getMyResumes(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<ResumeResponseDto> response = resumeService.getMyResumes(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 이력서 상세 조회", description = "이력서 ID를 통해 이력서의 모든 정보(경력, 학력, 자소서 등)를 조회합니다.")
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponseDto> getResumeDetail(
            @PathVariable("resumeId") Long resumeId, // ✨ ("resumeId") 명시적으로 추가
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ResumeResponseDto response = resumeService.getResumeDetail(resumeId, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "새 이력서 생성", description = "새로운 이력서와 하위 항목(경력, 자격증 등)을 한 번에 저장합니다.")
    @PostMapping
    public ResponseEntity<Long> createResume(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ResumeRequestDto requestDto) {

        Long createdResumeId = resumeService.createResume(userDetails.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResumeId);
    }

    @Operation(summary = "이력서 삭제", description = "이력서 및 관련된 모든 하위 항목을 삭제합니다.")
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable("resumeId") Long resumeId, //
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        resumeService.deleteResume(resumeId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "이력서 수정", description = "기존 이력서의 내용을 전체적으로 수정합니다.")
    @PutMapping("/{resumeId}")
    public ResponseEntity<Void> updateResume(
            @PathVariable("resumeId") Long resumeId, // ✨ 명시적 매핑 필수
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ResumeRequestDto requestDto) {

        resumeService.updateResume(resumeId, userDetails.getId(), requestDto);
        return ResponseEntity.ok().build();
    }
}