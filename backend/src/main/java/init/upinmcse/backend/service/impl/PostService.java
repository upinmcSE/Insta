package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.PostRequest;
import init.upinmcse.backend.dto.response.FileResponse;
import init.upinmcse.backend.dto.response.PostResponse;
import init.upinmcse.backend.enums.FileType;
import init.upinmcse.backend.enums.Status;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.model.Post;
import init.upinmcse.backend.model.PostLike;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.FileRepository;
import init.upinmcse.backend.repository.PostLikeRepository;
import init.upinmcse.backend.repository.PostRepostitory;
import init.upinmcse.backend.repository.UserRepository;
import init.upinmcse.backend.service.IFileService;
import init.upinmcse.backend.service.IPostService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PostService")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService implements IPostService {
    PostRepostitory postRepostitory;
    FileRepository fileRepository;
    IFileService fileService;
    UserRepository userRepository;
    PostLikeRepository postLikeRepository;

    @Override
    public PostResponse createPost(List<MultipartFile> files, PostRequest postRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        var post = Post.builder()
                .user(user)
                .caption(postRequest.getCaption())
                .status(Status.INACTIVE)
                .build();
        post = postRepostitory.save(post);

        List<FileResponse> fileResponses = new ArrayList<>();

        // save files
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    fileResponses.add(fileService.uploadFile(file, post.getId(), user.getId(), FileType.POST));
                } catch (Exception e) {
                    log.error("Error uploading file: {}", e.getMessage());
                    throw new ErrorException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                }
            }
        }
        post.setStatus(Status.ACTIVE);
        post = postRepostitory.saveAndFlush(post);

        return PostResponse.builder()
                .postId(post.getId())
                .userId(user.getId())
                .caption(post.getCaption())
                .fileUrls(fileResponses.stream().map(FileResponse::getUrl).toList())
                .build();
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepostitory.findById(postId)
                .orElseThrow(() -> new ErrorException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(post.getUser().getId()).orElseThrow(
                () -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        var files = fileRepository.findAllByPostId(postId);
        var likedUserIds = postLikeRepository.findAllByPostId(postId).stream()
                .map(postLike -> postLike.getUser().getId())
                .toList();

        return PostResponse.builder()
                .postId(post.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .avtUrl(user.getAvtUrl())
                .caption(post.getCaption())
                .fileUrls(files.stream().map(File::getUrl).toList())
                .likedUserIds(likedUserIds)
                .build();
    }

    @Override
    public PageResponse<PostResponse> getPostsByUserId(String userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = postRepostitory.findAllByUserIdAndStatus(user.getId(), Status.ACTIVE, pageable);

        var postList = pageData.getContent().stream().map(
            post -> PostResponse.builder()
                    .postId(post.getId())
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .avtUrl(user.getAvtUrl())
                    .caption(post.getCaption())
                    .fileUrls(fileRepository.findAllByPostId(post.getId()).stream()
                            .map(File::getUrl).toList())
                    .likedUserIds(postLikeRepository.findAllByPostId(post.getId()).stream().map(
                            postLike -> postLike.getUser().getId()).toList())
                    .build()).toList();


        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(postList)
                .build();
    }

    @Override
    public PageResponse<PostResponse> getNewPosts(int page, int size) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepostitory.findById(postId)
                .orElseThrow(() -> new ErrorException(ErrorCode.POST_NOT_FOUND));

        post.setStatus(Status.INACTIVE);
        postRepostitory.saveAndFlush(post);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        return null;
    }

    @Transactional
    @Override
    public void likePost(Long postId) {
        Post post = postRepostitory.findById(postId)
                .orElseThrow(() -> new ErrorException(ErrorCode.POST_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        // Check if the user has already liked the post
        if (postLikeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new ErrorException(ErrorCode.POST_ALREADY_LIKED);
        }
        // Create a new PostLike entity
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(postLike);
    }

    @Transactional
    @Override
    public void unlikePost(Long postId) {
        Post post = postRepostitory.findById(postId)
                .orElseThrow(() -> new ErrorException(ErrorCode.POST_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));


        if (!postLikeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new ErrorException(ErrorCode.POST_NOT_LIKED);
        }

        postLikeRepository.deleteByPostIdAndUserId(postId, user.getId());

    }
}
