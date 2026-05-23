package com.likelion.likelioncrud.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// OncePerRequestFilter: 하나의 요청에 대해 딱 한 번만 실행되는 필터
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

     // 토큰을 검증하고 SecurityContext에 인증 정보를 저장하는 역할
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값 추출
        // 클라이언트는 "Authorization: Bearer <토큰>" 형태로 요청을 보냄
        String authHeader = request.getHeader("Authorization");

        // 2. Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 토큰 검증 생략
        // (로그인, 회원가입 등 인증이 필요 없는 요청은 그냥 통과)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 다음 필터로 넘김
            return;
        }

        // 3. 토큰 값만 추출 (앞 7글자 "Bearer " 제거)
        String token = authHeader.substring(7);

        // 4. 토큰 유효성 검증 (만료 여부, 위변조 여부 확인)
        if (jwtUtil.validateToken(token)) {

            // 5. 토큰에서 userId 추출
            Long userId = jwtUtil.getUserId(token);

            // 6. userId로 Authentication 객체 생성
            // - 첫 번째 인자(principal): 현재 로그인한 사용자 정보 (userId)
            // - 두 번째 인자(credentials): 비밀번호 (토큰 방식에서는 불필요하므로 null)
            // - 세 번째 인자(authorities): 권한 목록 (현재는 빈 리스트)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            // 7. 요청 정보(IP, 세션 등)를 Authentication에 추가
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 8. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 9. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
