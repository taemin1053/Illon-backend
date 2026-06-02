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

        // 커넥션 타임아웃 (서버와 연결을 맺는 데 걸리는 시간 제한)
        factory.setConnectTimeout(3000);
        // 리드 타임아웃 (연결 후 데이터를 읽어오는 데 걸리는 시간 제한)
        factory.setReadTimeout(3000);

        return RestClient.builder()
                .baseUrl("https://port-0-illo-on-server-mp2oa3fkd20c6566.sel3.cloudtype.app")
                .requestFactory(factory)
                .build();
    }
}