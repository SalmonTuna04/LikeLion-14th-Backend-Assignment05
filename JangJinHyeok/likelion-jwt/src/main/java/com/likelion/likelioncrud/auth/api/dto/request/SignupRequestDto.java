package com.likelion.likelioncrud.auth.api.dto.request;

// 회원가입 요청 시 클라이언트에서 받는 데이터
public record SignupRequestDto(
        String name,
        String email,
        String password
) {
}
