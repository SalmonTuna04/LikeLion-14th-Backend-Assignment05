package com.likelion.likelioncrud.kakao.api;

import com.likelion.likelioncrud.auth.api.dto.response.LoginResponseDto;
import com.likelion.likelioncrud.common.response.code.SuccessCode;
import com.likelion.likelioncrud.common.template.ApiResTemplate;
import com.likelion.likelioncrud.kakao.application.KakaoOAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
@Tag(name = "카카오 로그인 API", description = "카카오 소셜 로그인 API")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/callback")
    public ApiResTemplate<LoginResponseDto> kakaoCallback(@RequestParam("code") String code) {
        LoginResponseDto response = kakaoOAuthService.kakaoLogin(code);
        return ApiResTemplate.successResponse(SuccessCode.LOGIN_SUCCESS, response);
    }
}