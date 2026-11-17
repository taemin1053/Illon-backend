package com.career.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortOneService {

    @Value("${portone.api.secret}")
    private String apiSecret;

    private final RestClient restClient = RestClient.create();

    /**
     * 프론트에서 넘겨준 인증 ID를 이용해 포트원 서버에서 실제 유저 정보를 가져옵니다.
     * V2 방식이므로 토큰 발급 없이 API Secret만 헤더에 넣으면 됩니다.
     */

    public Map<String, Object> getIdentityVerification(String verificationId) {
        // 🔥 [로컬 테스트용 방어막] verificationId가 "test-vId"로 들어오면 실제 포트원을 찌르지 않고 패스!
        if ("test-vId".equals(verificationId)) {
            log.info("⚙️ 로컬 테스트 모드: 가짜 본인인증 데이터를 반환합니다.");
            return Map.of(
                    "verifiedCustomer", Map.of(
                            "ci", "MOCK_USER_CI_TAEMIN_9999",
                            "name", "테스트유저",
                            "phoneNumber", "01012322113",
                            "birthDate", "2001-03-23",
                            "gender", "MALE"
                    )
            );
        }
        return restClient.get()
                .uri("https://api.portone.io/identity-verifications/" + verificationId)
                .header("Authorization", "PortOne " + apiSecret)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}