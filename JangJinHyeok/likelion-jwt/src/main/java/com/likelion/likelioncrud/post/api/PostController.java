package com.likelion.likelioncrud.post.api;

import com.likelion.likelioncrud.common.response.code.SuccessCode;
import com.likelion.likelioncrud.common.template.ApiResTemplate;
import com.likelion.likelioncrud.post.api.dto.request.PostSaveRequestDto;
import com.likelion.likelioncrud.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.likelioncrud.post.api.dto.response.PostInfoResponseDto;
import com.likelion.likelioncrud.post.application.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "POST API", description = "게시글 관리하는 api ")
public class PostController {

    private final PostService postService;

    // 게시물 저장
    @PostMapping()
    @Operation(summary = "게시물 저장", description = "게시물 저장.")
    public ApiResTemplate<Void> postSave(@RequestBody PostSaveRequestDto postSaveRequestDto) {
        postService.postSave(postSaveRequestDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.POST_SAVE_SUCCESS);
    }

    // 사용자 id를 기준으로 해당 사용자가 작성한 게시글 목록 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "게시물 memberId로 조회", description = "게시물 memberId로 조회")
    public ApiResTemplate<Page<PostInfoResponseDto>> myPostFindAll(@PathVariable("memberId") Long memberId, @ParameterObject @PageableDefault(size = 10, sort = "postId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PostInfoResponseDto> posts = postService.postFindMember(memberId, pageable);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, posts);
    }

    // 게시물 id를 기준으로 사용자가 작성한 게시물 수정
    @PatchMapping("/{postId}")
    @Operation(summary = "게시물 Id로 수정", description = "게시물 제목, 내용 수정")
    public ApiResTemplate<Void> postUpdate(@PathVariable("postId") Long postId,
                                             @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        postService.postUpdate(postId, postUpdateRequestDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.POST_UPDATE_SUCCESS);
    }

    // 게시물 id를 기준으로 사용자가 작성한 게시물 삭제
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시물 삭제", description = "게시물 Id로 삭제")
    public ApiResTemplate<Void> postDelete(@PathVariable("postId") Long postId) {
        postService.postDelete(postId);
        return ApiResTemplate.successWithNoContent(SuccessCode.POST_DELETE_SUCCESS);
    }
}