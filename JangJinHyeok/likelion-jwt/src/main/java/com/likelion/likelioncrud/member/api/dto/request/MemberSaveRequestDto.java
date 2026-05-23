package com.likelion.likelioncrud.member.api.dto.request;

import com.likelion.likelioncrud.member.domain.Part;
import jakarta.validation.constraints.*;

public record MemberSaveRequestDto(

        @NotBlank(message = "이름을 필수로 입력해야 합니다.")
        String name,

        @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
        @Max(value = 100, message = "나이는 100 이하이어야 합니다.")
        int age,

        @NotNull(message = "파트를 필수로 입력해야 합니다.")
        Part part
) {
}