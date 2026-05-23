package com.likelion.likelioncrud.post.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponseDto(
        List<PostInfoResponseDto> posts
) {
    public static PostListResponseDto from(List<PostInfoResponseDto> posts) {
        return PostListResponseDto.builder()
                .posts(posts)
                .build();
    }
}