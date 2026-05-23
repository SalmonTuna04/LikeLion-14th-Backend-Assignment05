package com.likelion.likelioncrud.member.api;

import com.likelion.likelioncrud.common.response.code.SuccessCode;
import com.likelion.likelioncrud.common.template.ApiResTemplate;
import com.likelion.likelioncrud.member.api.dto.request.MemberSaveRequestDto;
import com.likelion.likelioncrud.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.likelioncrud.member.api.dto.response.MemberInfoResponseDto;
import com.likelion.likelioncrud.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "멤버 API", description = "멤버 관리하는 api ")
public class MemberController {

    private final MemberService memberService;

    // 내 정보 조회 (토큰 필요)
    // JwtFilter에서 SecurityContext에 저장한 userId를 꺼냄
    // 토큰 없이 요청하면 401 Unauthorized 반환
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "JWT 토큰으로 현재 로그인한 사용자의 정보를 조회합니다.")
    public ApiResTemplate<MemberInfoResponseDto> memberFindMe(
            @AuthenticationPrincipal Long userId) {
        MemberInfoResponseDto response = memberService.memberFindMe(userId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

    // 사용자 저장
    @PostMapping()
    @Operation(summary = "멤버 회원가입", description = "멤버 회원가입 설명란입니다.")
    public ApiResTemplate<Void> memberSave(@RequestBody @Valid MemberSaveRequestDto memberSaveRequestDto) {
        memberService.memberSave(memberSaveRequestDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_SAVE_SUCCESS);
    }

    // 사용자 전체 조회
    @GetMapping("/all")
    @Operation(summary = "멤버 전체조회", description = "멤버 전체조회")
    public ApiResTemplate<Page<MemberInfoResponseDto>> memberFindAll(
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "memberId",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<MemberInfoResponseDto> members = memberService.memberFindAll(pageable);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS,members);
    }

    // 회원 id를 통해 특정 사용자 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "멤버 1명 조회", description = "멤버 id로 멤버조회")
    public ApiResTemplate<MemberInfoResponseDto> memberFindOne(@PathVariable("memberId") Long memberId) {
        MemberInfoResponseDto memberInfoResponseDto = memberService.memberFindOne(memberId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, memberInfoResponseDto);
    }

    // 회원 id를 통한 사용자 수정
    @PatchMapping("/{memberId}")
    @Operation(summary = "멤버 업데이트", description = "멤버 업데이트")
    public ApiResTemplate<Void> memberUpdate(@PathVariable("memberId") Long memberId,
                                             @RequestBody MemberUpdateRequestDto memberUpdateRequestDto) {
        memberService.memberUpdate(memberId, memberUpdateRequestDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_UPDATE_SUCCESS);
    }

    // 회원 id를 통한 사용자 삭제
    @DeleteMapping("/{memberId}")
    @Operation(summary = "멤버 삭제", description = "멤버 삭제")
    public ApiResTemplate<Void> memberDelete(@PathVariable("memberId") Long memberId) {
        memberService.memberDelete(memberId);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_DELETE_SUCCESS);
    }
}
