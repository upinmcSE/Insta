package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.PostRequest;
import init.upinmcse.backend.dto.response.PostResponse;
import init.upinmcse.backend.service.IPostService;
import init.upinmcse.backend.service.impl.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    IPostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseResponse<PostResponse> createPost(
            @RequestParam("file") List<MultipartFile> files,
            @ModelAttribute PostRequest postRequest
    ) {
        return BaseResponse.<PostResponse>builder()
                .message("Post created successfully")
                .result(postService.createPost(files, postRequest))
                .build();
    }

    @GetMapping("/{postId}")
    BaseResponse<PostResponse> getPost(@PathVariable Long postId) {
        return BaseResponse.<PostResponse>builder()
                .message("Post retrieved successfully")
                .result(postService.getPostById(postId))
                .build();
    }

    @GetMapping("/user/{userId}")
    BaseResponse<PageResponse<PostResponse>> getPostsByUser(@PathVariable String userId) {
        return BaseResponse.<PageResponse<PostResponse>>builder()
                .message("Posts retrieved successfully")
                .result(postService.getPostsByUserId(userId, 1, 10))
                .build();
    }

    @PatchMapping("/{postId}")
    BaseResponse<Void> updatePost(
            @PathVariable String postId,
            @RequestParam("file") List<MultipartFile> files,
            @ModelAttribute PostRequest postRequest
    ) {
        return BaseResponse.<Void>builder()
                .message("Post updated successfully")
                .build();
    }

    @DeleteMapping("/{postId}")
    BaseResponse<Void> deletePost(@PathVariable String postId) {
        return BaseResponse.<Void>builder()
                .message("Post deleted successfully")
                .build();
    }

    @DeleteMapping("/report/{postId}")
    BaseResponse<Void> reportPost(@PathVariable String postId) {
        return BaseResponse.<Void>builder()
                .message("Post reported successfully")
                .build();
    }

    @PostMapping("/like/{postId}")
    BaseResponse<Void> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return BaseResponse.<Void>builder()
                .message("Post liked successfully")
                .build();
    }

    @PostMapping("/unlike/{postId}")
    BaseResponse<Void> unlikePost(@PathVariable Long postId) {
        postService.unlikePost(postId);
        return BaseResponse.<Void>builder()
                .message("Post unliked successfully")
                .build();
    }
}
