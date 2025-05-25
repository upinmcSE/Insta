package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.request.CommentRequest;
import init.upinmcse.backend.dto.request.ReplyCommentRequest;
import init.upinmcse.backend.dto.response.CommentResponse;
import init.upinmcse.backend.dto.response.ReplyCommentResponse;

public interface ICommentService {
    CommentResponse createComment(CommentRequest request);
    CommentResponse updateComment(Long commentId, CommentRequest request);
    void deleteComment(Long commentId);

    ReplyCommentResponse createReply(ReplyCommentRequest request);
    ReplyCommentResponse updateReply(Long replyId, ReplyCommentRequest request);
    void deleteReply(Long replyId);
}
