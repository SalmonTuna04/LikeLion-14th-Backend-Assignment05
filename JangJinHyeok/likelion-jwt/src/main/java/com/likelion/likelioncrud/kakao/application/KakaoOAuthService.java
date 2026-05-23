package com.likelion.likelioncrud.kakao.application;

import com.likelion.likelioncrud.auth.JwtUtil;
import com.likelion.likelioncrud.auth.api.dto.response.LoginResponseDto;
import com.likelion.likelioncrud.auth.domain.RefreshToken;
import com.likelion.likelioncrud.auth.domain.RefreshTokenRepository;
import com.likelion.likelioncrud.common.exception.BusinessException;
import com.likelion.likelioncrud.common.response.code.ErrorCode;
import com.likelion.likelioncrud.kakao.api.dto.response.KakaoTokenResponse;
import com.likelion.likelioncrud.kakao.api.dto.response.KakaoUserInfoResponse;
import com.likelion.likelioncrud.member.domain.Member;
import com.likelion.likelioncrud.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoOAuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final RestClient restClient = RestClient.create();

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Transactional
    public LoginResponseDto kakaoLogin(String code) {

        KakaoTokenResponse tokenResponse = requestToken(code);

        KakaoUserInfoResponse userInfoResponse =
                requestUserInfo(tokenResponse.accessToken());

        String email = userInfoResponse.getEmail();
        String nickname = userInfoResponse.getNickname();

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .name(nickname)
                                .email(email)
                                .password(null)
                                .build()
                ));

        String accessToken = jwtUtil.generateToken(member.getMemberId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());
        // 백엔드 서버가 토큰 자체발급

        // 리프레쉬 토큰 삭제
        refreshTokenRepository.deleteByMemberId(member.getMemberId());

        // 리프레쉬 토큰 재발급
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .memberId(member.getMemberId())
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();


        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    // 리퀘스트토큰 메소드
    private KakaoTokenResponse requestToken(String code) {
        try {
            return restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(
                            "grant_type=authorization_code"
                                    + "&client_id=" + clientId
                                    + "&client_secret=" + clientSecret
                                    + "&redirect_uri=" + redirectUri
                                    + "&code=" + code
                    )
                    .retrieve()
                    .body(KakaoTokenResponse.class);

        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.KAKAO_LOGIN_FAILED_EXCEPTION,
                    ErrorCode.KAKAO_LOGIN_FAILED_EXCEPTION.getMessage()
            );
        }
    }

    private KakaoUserInfoResponse requestUserInfo(String accessToken) {
        try {
            return restClient.get()
                    .uri(userInfoUri)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(KakaoUserInfoResponse.class);

        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.KAKAO_LOGIN_FAILED_EXCEPTION,
                    ErrorCode.KAKAO_LOGIN_FAILED_EXCEPTION.getMessage()
            );
        }
    }
}
