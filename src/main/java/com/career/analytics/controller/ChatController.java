package com.career.analytics.controller;

import com.career.analytics.dto.ChatRequestDto;
import com.career.analytics.dto.AiChatResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Chatbot API", description = "AI 챗봇 중계 프록시 API")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatbotService chatbotService;

    @Operation(
            summary = "AI 챗봇 메시지 전송 및 중계",
            description = "프론트엔드 대화 메시지를 수신하여 로그인된 유저 프로필 정보를 바인딩한 후, AI 서버로 전송 및 응답을 중계합니다."
    )
    @PostMapping
    public ResponseEntity<AiChatResponseDto> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChatRequestDto request
    ) {
        log.info("📢 [ChatController] AI 챗봇 메시지 중계 API 호출 - 유저 ID: {}", userDetails.getId());
        AiChatResponseDto response = chatbotService.sendToAiServer(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }
}
