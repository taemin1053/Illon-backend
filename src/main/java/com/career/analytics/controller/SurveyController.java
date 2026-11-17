package com.career.analytics.controller;

import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Survey API", description = "사전 설문조사 관련 API")
@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    // 🔥 핵심: SurveyService를 스프링이 주입(Injection)할 수 있도록 선언해야 합니다.
    private final SurveyService surveyService;

    @Operation(summary = "설문조사 완료 처리", description = "현재 로그인한 유저의 설문조사 상태를 완료(true)로 변경합니다.")
    @PostMapping("/complete") // 프론트에서 호출할 주소: /api/survey/complete
    public ResponseEntity<Void> completeSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        // SurveyService의 completeSurvey 메서드를 호출하여 로직 수행
        surveyService.completeSurvey(userDetails.getId());

        return ResponseEntity.ok().build();
    }
}