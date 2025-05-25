package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.request.CommentRequest;
import init.upinmcse.backend.dto.request.ReplyCommentRequest;
import init.upinmcse.backend.dto.response.CommentResponse;
import init.upinmcse.backend.dto.response.ReplyCommentResponse;
import init.upinmcse.backend.service.ICommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    ICommentService commentService;

    @PostMapping()
    public BaseResponse<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return BaseResponse.<CommentResponse>builder()
                .message("Comment created successfully")
                .result(commentService.createComment(request))
                .build();
    }

    @PostMapping("/reply")
    public BaseResponse<ReplyCommentResponse> replyToComment(@RequestBody ReplyCommentRequest request) {
        return BaseResponse.<ReplyCommentResponse>builder()
                .message("Reply created successfully")
                .result(commentService.createReply(request))
                .build();
    }

    @PutMapping()
    public String updateComment() {
        return "Comment updated successfully";
    }

    @PutMapping("/reply")
    public String updateReply() {
        return "Reply updated successfully";
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment() {
        return "Comment deleted successfully";
    }

    @DeleteMapping("/reply/{replyId}")
    public String deleteReply() {
        return "Reply deleted successfully";
    }
}
