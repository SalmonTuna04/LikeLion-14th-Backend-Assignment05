package com.likelion.likelioncrud.auth.api;

import com.likelion.likelioncrud.auth.api.dto.request.LoginRequestDto;
import com.likelion.likelioncrud.auth.api.dto.request.SignupRequestDto;
import com.likelion.likelioncrud.auth.api.dto.request.TokenRefreshRequestDto;
import com.likelion.likelioncrud.auth.api.dto.response.LoginResponseDto;
import com.likelion.likelioncrud.auth.api.dto.response.TokenRefreshResponseDto;
import com.likelion.likelioncrud.auth.application.AuthService;
import com.likelion.likelioncrud.common.response.code.SuccessCode;
import com.likelion.likelioncrud.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "로그인 API", description = "회원가입, 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResTemplate<Void> signup(@RequestBody SignupRequestDto request) {
        authService.signup(request);
        return ApiResTemplate.successWithNoContent(SuccessCode.SIGNUP_SUCCESS);
    }

    // 로그인
    @PostMapping("/login")
    public ApiResTemplate<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ApiResTemplate.successResponse(SuccessCode.LOGIN_SUCCESS, response);
    }

    // [과제] Access Token 재발급
    // Refresh Token을 받아서 새로운 Access Token을 발급하는 API
    @PostMapping("/refresh")
    public ApiResTemplate<TokenRefreshResponseDto> refresh(@RequestBody TokenRefreshRequestDto request) {
        TokenRefreshResponseDto response = authService.reissue(request);
        return ApiResTemplate.successResponse(SuccessCode.TOKEN_REISSUE_SUCCESS, response);
    }
}
