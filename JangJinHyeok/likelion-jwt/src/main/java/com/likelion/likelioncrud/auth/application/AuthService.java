package com.likelion.likelioncrud.auth.application;

import com.likelion.likelioncrud.auth.JwtUtil;
import com.likelion.likelioncrud.auth.api.dto.request.LoginRequestDto;
import com.likelion.likelioncrud.auth.api.dto.request.SignupRequestDto;
import com.likelion.likelioncrud.auth.api.dto.request.TokenRefreshRequestDto;
import com.likelion.likelioncrud.auth.api.dto.response.LoginResponseDto;
import com.likelion.likelioncrud.auth.api.dto.response.TokenRefreshResponseDto;
import com.likelion.likelioncrud.auth.domain.RefreshToken;
import com.likelion.likelioncrud.auth.domain.RefreshTokenRepository;
import com.likelion.likelioncrud.common.exception.BusinessException;
import com.likelion.likelioncrud.common.response.code.ErrorCode;
import com.likelion.likelioncrud.member.domain.Member;
import com.likelion.likelioncrud.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션 적용 (조회 성능 최적화)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  // SecurityConfig에서 빈으로 등록한 BCryptPasswordEncoder
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // application.yml의 jwt.refresh-expiration 값 (밀리초)
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 회원가입
    @Transactional  // DB에 저장하는 작업이므로 쓰기 트랜잭션 적용
    public void signup(SignupRequestDto request) {

        // 1. 이메일 중복 체크
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_EXCEPTION, ErrorCode.DUPLICATE_EMAIL_EXCEPTION.getMessage());
        }

        // 2. 비밀번호 BCrypt 암호화 후 Member 생성
        Member member = Member.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))  // 비밀번호 암호화
                .build();

        // 3. DB 저장
        memberRepository.save(member);
    }


    // 로그인
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {

        // 1. 이메일로 회원 조회 (없으면 예외 처리)
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL_EXCEPTION, ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL_EXCEPTION.getMessage()));

        // 2. 입력한 비밀번호와 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_EXCEPTION, ErrorCode.INVALID_PASSWORD_EXCEPTION.getMessage());
        }

        // 3. 인증 성공 → Access Token 발급
        String accessToken = jwtUtil.generateToken(member.getMemberId());

        // [과제] Refresh Token 발급 및 DB 저장
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

        refreshTokenRepository.deleteByMemberId(member.getMemberId()); // jwtUtil 객체의 generateRefreshToken을 호출하여 토큰을 발급함.

        RefreshToken refreshTokenEntity = RefreshToken.builder() // 중복 로그인이나 토큰 유출 방지 및 DB 자원 낭비를 줄이기 위해 기존 사용자의 기존 Refresh Token을 완전 삭제함.
                .memberId(member.getMemberId())
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000)) // 밀리초 단위 데이터를 1000으로 나누어 초단위로 바꾸고 만료 날짜를 계산해 안전하게 함.
                .build();

        refreshTokenRepository.save(refreshTokenEntity);


        // 4. Access Token + Refresh Token 반환
        return new LoginResponseDto(accessToken, refreshToken);
    }

    // [과제] Access Token 재발급
    @Transactional
    public TokenRefreshResponseDto reissue(TokenRefreshRequestDto request) {

        String refreshToken = request.refreshToken(); // 클라이언트가 Body로 전송해준 요청 데이터(Dto)에서 검증용 refresHToken 스트링 정보를 꺼냄.

        if (!jwtUtil.validateToken(refreshToken)) { // jwtUtil.validateToken을 통해 해당 토큰이 서버의 시크릿 키와 맞는지, 만료일은 지나지 않았는지 체크하고 에러를 냄.
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION, ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken) // 토큰의 무결성이 통과되면 화이트리스트 방식 검증을 위해 실제 데이터베이스에 유효한 값으로 매칭되는 기록이 존재하는지 찾음.
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION, ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION.getMessage()));

        Long userId = jwtUtil.getUserId(refreshToken); // 데이터가 완벽히 증명되면 Payload에서 유저의 고유 Primary Key인 userId를 복원해 Access Token을 재발급함.
        String newAccessToken = jwtUtil.generateToken(userId);

        // 5. 새로 발급한 Access Token 반환
        return new TokenRefreshResponseDto(newAccessToken);
    }
}
