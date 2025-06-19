package init.upinmcse.backend.repository.impl;

import init.upinmcse.backend.dto.request.CommentRequest;
import init.upinmcse.backend.dto.request.ReplyCommentRequest;
import init.upinmcse.backend.dto.response.CommentResponse;
import init.upinmcse.backend.dto.response.ReplyCommentResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.Comment;
import init.upinmcse.backend.model.Post;
import init.upinmcse.backend.model.RepComment;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.CommentRepository;
import init.upinmcse.backend.repository.PostRepostitory;
import init.upinmcse.backend.repository.RepCommentRepository;
import init.upinmcse.backend.repository.UserRepository;
import init.upinmcse.backend.service.ICommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {
    CommentRepository commentRepository;
    RepCommentRepository repCommentRepository;
    PostRepostitory postRepostitory;
    UserRepository userRepository;

    @Override
    public CommentResponse createComment(CommentRequest request) {
        Post post = postRepostitory.findById(request.getPostId())
                .orElseThrow(() -> new ErrorException(ErrorCode.POST_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();
        comment = commentRepository.save(comment);

        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(post.getId())
                .userId(user.getId())
                .fullName(user.getUserProfile().getFullName())
                .avtUrl(user.getUserProfile().getAvtUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ErrorException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);

    }

    @Override
    public ReplyCommentResponse createReply(ReplyCommentRequest request) {
        // check comment exists
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new ErrorException(ErrorCode.COMMENT_NOT_FOUND));

        // check user exists
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        // create reply comment
        RepComment repComment = RepComment.builder()
                .content(request.getContent())
                .comment(comment)
                .user(user)
                .build();
        return ReplyCommentResponse.builder()
                .replyId(repComment.getId())
                .commentId(comment.getId())
                .content(repComment.getContent())
                .userId(user.getId())
                .fullName(user.getUserProfile().getFullName())
                .avtUrl(user.getUserProfile().getAvtUrl())
                .build();
    }

    @Override
    public ReplyCommentResponse updateReply(Long replyId, ReplyCommentRequest request) {
        return null;
    }

    @Override
    public void deleteReply(Long replyId) {
        RepComment repComment = repCommentRepository.findById(replyId)
                .orElseThrow(() -> new ErrorException(ErrorCode.REPLY_COMMENT_NOT_FOUND));
        repCommentRepository.delete(repComment);
    }
}
