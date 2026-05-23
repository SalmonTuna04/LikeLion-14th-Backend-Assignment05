package com.likelion.likelioncrud.auth.api.dto.request;

// Access Token 재발급 요청 시 클라이언트가 보내는 데이터
public record TokenRefreshRequestDto(
        String refreshToken  // 로그인 시 발급받은 Refresh Token
) {
}
