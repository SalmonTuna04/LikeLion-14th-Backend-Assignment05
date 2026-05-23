package com.likelion.likelioncrud.member.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberListResponseDto(
        List<MemberInfoResponseDto> members
) {
    public static MemberListResponseDto from(
            List<MemberInfoResponseDto> members) {
        return MemberListResponseDto.builder()
                .members(members)
                .build();
    }
}