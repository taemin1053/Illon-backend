package com.career.analytics.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // 1. java.security.Key -> javax.crypto.SecretKey 로 변경 완료
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key; // 2. 타입 변경 완료
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId())) // setSubject -> subject
                .issuedAt(now) // setIssuedAt -> issuedAt
                .expiration(expiryDate) // setExpiration -> expiration
                .signWith(key) // 0.12.x 최신 권장 방식 (알고리즘 자동 추론)
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser() // parserBuilder -> parser
                .verifyWith(key) // setSigningKey -> verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws -> parseSignedClaims
                .getPayload(); // getBody -> getPayload

        return Long.parseLong(claims.getSubject());
    }

    // 토큰 유효성 검증
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}