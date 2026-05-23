package com.likelion.likelioncrud.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Refresh Token을 DB에 저장하는 엔티티
// 로그인 시 발급된 refresh token을 DB에 보관하고,
// /auth/refresh 요청 시 DB 값과 비교해서 유효성을 검증함
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 사용자의 refresh token인지 (Member 엔티티와 직접 조인하지 않고 id만 저장)
    @Column(nullable = false)
    private Long memberId;

    // DB에 저장되는 refresh token 문자열
    @Column(nullable = false, unique = true)
    private String token;

    // refresh token 만료 시각
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private RefreshToken(Long memberId, String token, LocalDateTime expiredAt) {
        this.memberId = memberId;
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
