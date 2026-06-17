package com.career.analytics.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter implements Filter {

    // IP별 버킷을 스레드 안전하게 관리하기 위한 캐시 맵
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // IP당 1초에 최대 5번 요청 임계치(대역폭) 정의
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(1))))
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 💡 GET /api/jobs API 요청에 대해서만 Rate Limiting 활성화
        if ("/api/jobs".equals(path) && "GET".equalsIgnoreCase(method)) {
            String ip = getClientIP(httpRequest);
            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            // 1개의 토큰 소모 시도 (획득 실패 시 429 에러 처리)
            if (!bucket.tryConsume(1)) {
                log.warn("⚠️ [Rate Limit Exceeded] 요청 한도 초과 - IP: {}, Path: {}", ip, path);
                
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write(
                    "{\"error\": \"Too Many Requests\", \"message\": \"단시간에 너무 많은 요청을 보냈습니다. 1초 후에 다시 시도해 주세요.\"}"
                );
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * 프록시 환경(Nginx, Cloudflare 등)을 고려한 클라이언트의 실제 IP 추출 메서드
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
