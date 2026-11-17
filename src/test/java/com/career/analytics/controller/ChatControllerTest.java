package com.career.analytics.controller;

import com.career.analytics.dto.AiChatResponseDto;
import com.career.analytics.dto.ChatRequestDto;
import com.career.analytics.entity.Role;
import com.career.analytics.entity.Users;
import com.career.analytics.security.CustomUserDetailsService;
import com.career.analytics.security.JwtTokenProvider;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.ChatbotService;
import com.career.analytics.service.CustomOAuth2UserService;
import com.career.analytics.security.OAuth2SuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatbotService chatbotService;

    // SecurityConfig 자동 인스턴스화 예외 방지를 위한 모킹 빈 등록
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    void setUp() {
        // 커스텀 UserDetailsImpl을 정적 팩토리 build() 메서드를 통해 인스턴스화하여 Security Context에 로드
        Users fakeUser = Users.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.USER)
                .build();
        UserDetailsImpl userDetails = UserDetailsImpl.build(fakeUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void sendMessage_ShouldReturnOkAndMatchingSpec_WhenRequestIsValid() throws Exception {
        // given
        ChatRequestDto requestDto = ChatRequestDto.builder()
                .message("백엔드 개발자 공고 추천해줘")
                .isQuickReplies(false)
                .quickRepliesCategory(null)
                .history(List.of())
                .build();

        AiChatResponseDto mockResponse = AiChatResponseDto.builder()
                .answer("추천해 드리는 백엔드 개발자 채용 공고 목록입니다...")
                .intent("job_recommendation")
                .sources(List.of("source1", "source2"))
                .userMessage(Map.of("role", "user", "content", "백엔드 개발자 공고 추천해줘"))
                .assistantMessage(Map.of("role", "assistant", "content", "추천해 드리는 백엔드 개발자 채용 공고 목록입니다..."))
                .build();

        Mockito.when(chatbotService.sendToAiServer(eq(1L), any(ChatRequestDto.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("추천해 드리는 백엔드 개발자 채용 공고 목록입니다..."))
                .andExpect(jsonPath("$.intent").value("job_recommendation"))
                .andExpect(jsonPath("$.sources[0]").value("source1"))
                .andExpect(jsonPath("$.user_message.role").value("user"))
                .andExpect(jsonPath("$.assistant_message.role").value("assistant"));
    }
}
