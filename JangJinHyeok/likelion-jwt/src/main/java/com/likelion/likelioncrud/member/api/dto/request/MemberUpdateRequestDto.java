package com.likelion.likelioncrud.member.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequestDto(

        @NotBlank(message = "이름을 필수로 입력해야 합니다.")
        String name,

        @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
        @Max(value = 100, message = "나이는 100 이하이어야 합니다.")
        int age
) {
}