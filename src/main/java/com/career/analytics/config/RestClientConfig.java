package com.career.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient aiServerRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 커넥션 및 리드 타임아웃 30초 설정 - 상향 유지
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(30000);

        String baseUrl = "https://port-0-illo-on-server-mp2oa3fkd20c6566.sel3.cloudtype.app";
        //String baseUrl = "http://localhost:8000";
        // 💡 주소 결합 시 슬래시(/)가 누락되거나 중복되는 현상을 방어하기 위한 정규화
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .requestInterceptor(new RestClientLoggingInterceptor()) // 💡 디버깅 로그 인터셉터 장착
                .build();
    }
}