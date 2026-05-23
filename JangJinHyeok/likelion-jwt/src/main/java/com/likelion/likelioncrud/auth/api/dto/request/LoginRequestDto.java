package com.likelion.likelioncrud.auth.api.dto.request;

// 로그인 요청 시 클라이언트에서 받는 데이터
public record LoginRequestDto(
        String email,
        String password
) {
}
