package com.career.analytics.service;

import com.career.analytics.dto.AiChatRequestDto;
import com.career.analytics.dto.AiChatResponseDto;
import com.career.analytics.dto.ChatRequestDto;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.AiServerException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final UserRepository userRepository;
    private final RestClient aiServerRestClient;

    /**
     * 프론트엔드 챗봇 요청을 AI 서버 스펙에 맞게 DTO로 조립하여 AI 서버로 전송 및 응답을 중계합니다.
     */
    public AiChatResponseDto sendToAiServer(Long userId, ChatRequestDto request) {
        log.info("📢 [ChatbotService] AI 챗봇 프록시 요청 시작 - 유저 ID: {}", userId);

        // 1. 유저 정보 및 프로필 데이터 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Profile profile = user.getProfile();

        // 2. UserInfo 정보 조립 (현재 스펙 상 제공 가능한 careerLevel만 주입하고 나머지는 null로 설정)
        AiChatRequestDto.UserInfo userInfo = AiChatRequestDto.UserInfo.builder()
                .careerType(profile != null && profile.getCareerLevel() != null ? profile.getCareerLevel().name() : null)
                .jobType(null)
                .region(null)
                .occupation(null)
                .education(null)
                .university(null)
                .major(null)
                .careerYears(null)
                .companyName(null)
                .build();

        // 3. AI 서버 전송용 DTO 생성
        AiChatRequestDto aiRequestDto = AiChatRequestDto.builder()
                .message(request.getMessage())
                .userId(userId.toString()) // UUID 호환을 위한 String 형변환 주입
                .isQuickReplies(request.isQuickReplies())
                .quickRepliesCategory(request.getQuickRepliesCategory())
                .history(request.getHistory())
                .userInfo(userInfo)
                .build();

        // 4. RestClient를 통해 AI 서버 통신 중계
        try {
            log.info("🌐 [ChatbotService] AI 서버로 챗봇 요청 전송 (URI: /chat)");
            AiChatResponseDto responseDto = aiServerRestClient.post()
                    .uri("/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(aiRequestDto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                        log.error("❌ [Chatbot API 4xx] AI 챗봇 요청 포맷 에러 - 상태코드: {}", res.getStatusCode());
                        throw new AiServerException("AI 챗봇 요청 형식이 올바르지 않습니다.");
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                        log.error("❌ [Chatbot API 5xx] AI 챗봇 서버 장애 - 상태코드: {}", res.getStatusCode());
                        throw new AiServerException("AI 챗봇 서비스 장애가 발생했습니다.");
                    })
                    .body(AiChatResponseDto.class);

            log.info("📢 [ChatbotService] AI 서버 응답 수신 성공");
            return responseDto;

        } catch (Exception e) {
            log.error("❌ [ChatbotService] AI 챗봇 통신 장애 발생. 에러: {}", e.getMessage(), e);
            if (e instanceof AiServerException) {
                throw (AiServerException) e;
            }
            throw new AiServerException("AI 챗봇 서버와의 통신 중 에러가 발생했습니다.", e);
        }
    }
}
