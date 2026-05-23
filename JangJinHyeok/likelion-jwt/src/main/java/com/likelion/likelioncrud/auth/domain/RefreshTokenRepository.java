package com.likelion.likelioncrud.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // token 문자열로 RefreshToken 엔티티 조회
    Optional<RefreshToken> findByToken(String token);

    // 해당 사용자의 기존 refresh token 삭제 (로그인 시 재발급 전에 호출)
    void deleteByMemberId(Long memberId);
}
