package com.career.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
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
        return restClient.get()
                .uri("https://api.portone.io/identity-verifications/" + verificationId)
                .header("Authorization", "PortOne " + apiSecret)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}