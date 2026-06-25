package com.career.analytics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 💡 실제 요청이 전송되는 최종 Absolute URL을 로깅합니다.
        log.info("🌐 [RestClient Request] Send -> HTTP Method: {}, URL: {}", request.getMethod(), request.getURI());
        
        ClientHttpResponse response = execution.execute(request, body);
        
        log.info("🌐 [RestClient Response] Recv <- HTTP Status: {}, URI: {}", response.getStatusCode(), request.getURI());
        return response;
    }
}
