package com.likelion.likelioncrud.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

// @EnableWebSecurity: Spring Security 활성화
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF(Cross-Site Request Forgery) 보호 비활성화
                // JWT는 stateless 방식이라 세션/쿠키를 사용하지 않으므로 CSRF 불필요
                .csrf(AbstractHttpConfigurer::disable)

                // JWT 방식은 서버에 세션을 저장하지 않으므로 세션 사용 안 함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 인가(Authorization) 설정
                .authorizeHttpRequests(auth -> auth

                        // 아래 경로들은 토큰 없이 누구나 접근 가능
                        .requestMatchers(
                                "/auth/signup",      // 회원가입 API
                                "/auth/login",       // 로그인 API
                                "/auth/refresh",     // Access Token 재발급 API
                                "/swagger-ui/**",   // Swagger UI 페이지
                                "/auth/kakao/login",
                                "/auth/kakao/**",
                                "/v3/api-docs/**"    // Swagger API 문서
                        ).permitAll()

                        // 위에서 허용한 경로 외 나머지는 모두 인증(토큰) 필요
                        .anyRequest().authenticated()
                )

                // 인증 실패 시 403 대신 401 반환하도록 설정
                // 기본값은 403이지만 토큰이 없는 경우는 401이 맞음
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                // JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 등록
                // → 모든 요청에서 컨트롤러 도달 전에 JWT 검증이 먼저 실행됨
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
