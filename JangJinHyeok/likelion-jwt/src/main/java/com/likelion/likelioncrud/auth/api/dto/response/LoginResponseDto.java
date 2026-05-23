package com.likelion.likelioncrud.auth.api.dto.response;

// 로그인 성공 시 클라이언트에게 반환하는 데이터
public record LoginResponseDto(
        String accessToken,   // 발급된 JWT Access Token (30분 유효)
        String refreshToken   // 발급된 JWT Refresh Token (7일 유효)
) {
}
