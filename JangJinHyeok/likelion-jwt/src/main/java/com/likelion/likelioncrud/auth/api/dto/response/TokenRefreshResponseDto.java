package com.likelion.likelioncrud.auth.api.dto.response;

// Access Token 재발급 성공 시 클라이언트에게 반환하는 데이터
public record TokenRefreshResponseDto(
        String accessToken  // 새로 발급된 Access Token
) {
}
