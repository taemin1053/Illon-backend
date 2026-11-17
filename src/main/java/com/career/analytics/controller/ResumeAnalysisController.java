package com.career.analytics.controller;

import com.career.analytics.dto.AiAnalysisResponseDto;
import com.career.analytics.service.ResumeAnalysisApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Resume Analysis API", description = "이력서 AI 분석 및 피드백 API")
@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeAnalysisController {

    private final ResumeAnalysisApplicationService resumeAnalysisApplicationService;

    @Operation(
            summary = "이력서 AI 분석 및 결과 저장",
            description = "사용자의 이력서 데이터를 DB에서 조회하여 AI 분석 서버로 전송하고 평가한 뒤, 결과를 DB에 영속화하고 반환합니다."
    )
    @PostMapping("/{resumeId}/analyze")
    public ResponseEntity<AiAnalysisResponseDto> analyzeResume(
            @PathVariable Long resumeId
    ) {
        log.info("📢 [Controller] 이력서 AI 분석 API 호출 - 이력서 ID: {}", resumeId);
        
        AiAnalysisResponseDto response = resumeAnalysisApplicationService.analyzeAndSave(resumeId);
        
        return ResponseEntity.ok(response);
    }
}
