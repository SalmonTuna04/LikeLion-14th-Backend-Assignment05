package com.likelion.likelioncrud.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateRequestDto(

        @NotBlank(message = "제목을 필수로 입력해야 합니다.")
        String title,

        @NotBlank(message = "내용을 필수로 입력해야 합니다.")
        String contents
) {
}