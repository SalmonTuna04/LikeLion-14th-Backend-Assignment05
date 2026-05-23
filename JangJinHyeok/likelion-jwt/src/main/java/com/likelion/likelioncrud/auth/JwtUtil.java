package com.likelion.likelioncrud.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // application.yml의 jwt.secret 값을 자동으로 주입
    @Value("${jwt.secret}")
    private String secretKey;

    // application.yml의 jwt.expiration 값을 자동으로 주입
    @Value("${jwt.expiration}")
    private long expiration;

    // application.yml의 jwt.refresh-expiration 값을 자동으로 주입 (추가 바인딩 적용)
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Access Token 생성
    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))                                       // payload에 userId 저장 (subject는 String 타입)
                .issuedAt(new Date())                                                  // 토큰 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiration))         // 토큰 만료 시간
                .signWith(getSigningKey())                                             // Secret Key로 서명
                .compact();                                                            // 토큰 문자열로 변환
    }

    // [과제] Refresh Token 생성
    // generateToken()을 참고해서 refreshExpiration을 사용하도록 구현하세요.
    public String generateRefreshToken(Long userId) {
        return Jwts.builder() // Jwts.builder()를 통해 새로운 jwt 토큰을 빌드.
                .subject(String.valueOf(userId)) // 사용자의 고유 ID(userId)를 문자열 형태로 저장해 토큰의 주인을 식별함
                .issuedAt(new Date()) // 현재 토큰이 발급된 시각을 명시함.
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration)) // 현재 시간에 yml에서 주입받은 'refreshExpiration(7일)'을 더해 만료일을 세팅함.
                .signWith(getSigningKey()) // 암호화 키인 SecretKey로 안전하게 디지털 서명을 진행ㅎ마
                .compact();
    }

    // 토큰 파싱
    public Long getUserId(String token) {
        // subject에 저장된 userId를 String → Long으로 변환해서 반환
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // 토큰 유효성 검증
    // 유효하면 true, 만료 or 위변조 등이면 false
    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 파싱 시 만료, 위변조 등이면 예외 발생
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //payload 파싱 (userId를 꺼내서 누가 보낸 요청인지 알기 위해서)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Secret Key로 서명 검증
                .build()
                .parseSignedClaims(token)   // 토큰 파싱
                .getPayload();              // payload(Claims) 반환
    }

    // application.yml의 secret 문자열을 SecretKey 객체로 변환
    private SecretKey getSigningKey() {
        // Base64로 인코딩된 secret 문자열을 디코딩해서 SecretKey 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}