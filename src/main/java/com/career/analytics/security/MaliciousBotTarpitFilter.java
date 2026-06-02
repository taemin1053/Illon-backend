package com.career.analytics.security;
import com.career.analytics.config.SecurityConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class MaliciousBotTarpitFilter extends OncePerRequestFilter {

    // 해커 봇들이 주로 쑤시는 대표적인 악성 취약점 경로 블랙리스트
    private final List<String> blacklistPatterns = List.of(
            "/wp-admin",
            "/wp-login.php",
            "/.env",
            "/xmlrpc.php",
            "/actuator",
            "install.php"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String clientIP = getClientIP(request);

        // 1. 요청 URI가 블랙리스트 패턴을 포함하는지 검사
        boolean isBot = blacklistPatterns.stream().anyMatch(requestURI::contains);

        if (isBot) {
            log.warn(" [봇 탐지] IP: {}, Path: {} -> 10초간 대기 시킵니다.", clientIP, requestURI);

            try {
                // 30초 동안 응답을 주지 않고 연결을 강제로 쥐고 늘어집니다 (Tarpit)
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 시간 질질 끌고 나서 규격 외 요청으로 간주하여 에러 응답 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Request.");
            return; // 시큐리티 필터 체인을 타지 못하게 여기서 요청을 완전 종료합니다.
        }

        // 정상 요청은 그대로 통과
        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}