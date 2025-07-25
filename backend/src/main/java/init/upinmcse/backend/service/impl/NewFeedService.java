package init.upinmcse.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;
import init.upinmcse.backend.constant.Status;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.model.Post;
import init.upinmcse.backend.repository.cache.impl.NewFeedRedis;
import init.upinmcse.backend.repository.db.FileRepository;
import init.upinmcse.backend.repository.db.PostLikeRepository;
import init.upinmcse.backend.repository.db.PostRepository;
import init.upinmcse.backend.service.INewFeedService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "NewFeedService")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewFeedService implements INewFeedService {
    PostRepository postRepository;
    FileRepository fileRepository;
    PostLikeRepository postLikeRepository;
    NewFeedRedis newFeedRedis;
    ObjectMapper objectMapper;
    RedisTemplate<String, Object> redisTemplate;
    static final long TTL_SECONDS = 7 * 24 * 60 * 60;

    @Override
    public PageResponse<PostResponse> getDynamicPost(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = postRepository.findAllByStatus(Status.ACTIVE, pageable);

        var postList = pageData.getContent().stream().map(
                post -> PostResponse.builder()
                        .postId(post.getId())
                        .userId(post.getUser().getId())
                        .fullName(post.getUser().getFullName())
                        .avtUrl(post.getUser().getAvtUrl())
                        .caption(post.getCaption())
                        .fileUrls(fileRepository.findAllByPostId(post.getId()).stream()
                                .map(File::getUrl).toList())
                        .likedUserIds(postLikeRepository.findAllByPostId(post.getId()).stream().map(
                                postLike -> postLike.getUser().getId()).toList())
                        .createdAt(post.getCreatedAt())
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
    public PageResponse<PostResponse> getPrecomputedPost(int page, int size, String userId) {
        try {

            String redisKey = "new_feed:" + userId;

            // Tính toán start và end cho phân trang
            long start = (long) (page - 1) * size;
            long end = start + size - 1;

            List<Object> posts = redisTemplate.opsForList().range(redisKey, start, end);
            if (posts != null && !posts.isEmpty()) {
                List<PostResponse> postResponseList = posts.stream()
                        .map(postId -> {
                            try {
                                return objectMapper.readValue(postId.toString(), PostResponse.class);
                            } catch (Exception e) {
                                log.error("Error parsing postId from Redis: {}", postId, e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

                if (postResponseList.isEmpty()) {
                    log.warn("No valid posts deserialized from Redis for key: {}", redisKey);
                } else {
                    Long totalElements = redisTemplate.opsForList().size(redisKey);
                    if (totalElements == null) {
                        totalElements = 0L;
                    }

                    return PageResponse.<PostResponse>builder()
                            .currentPage(page)
                            .pageSize(size)
                            .totalPages((int) Math.ceil((double) totalElements / size))
                            .totalElements(totalElements)
                            .data(postResponseList)
                            .build();
                }
            }

            Sort sort = Sort.by("createdAt").descending();
            Pageable pageable = PageRequest.of(page - 1, size, sort);
            Page<Post> pageData = postRepository.findAllByFollowingUserIdAndStatus(userId, Status.ACTIVE, pageable);

            var postList = pageData.getContent().stream().map(
                    post -> PostResponse.builder()
                            .postId(post.getId())
                            .userId(post.getUser().getId())
                            .fullName(post.getUser().getFullName())
                            .avtUrl(post.getUser().getAvtUrl())
                            .caption(post.getCaption())
                            .fileUrls(fileRepository.findAllByPostId(post.getId()).stream()
                                    .map(File::getUrl).toList())
                            .likedUserIds(postLikeRepository.findAllByPostId(post.getId()).stream()
                                    .map(postLike -> postLike.getUser().getId()).toList())
                            .createdAt(post.getCreatedAt())
                            .build()).toList();

            // Xử lý trường hợp rỗng
            if (pageData.getTotalElements() == 0) {
                return PageResponse.<PostResponse>builder()
                        .currentPage(page)
                        .pageSize(size)
                        .totalPages(0)
                        .totalElements(0)
                        .data(Collections.emptyList())
                        .build();
            }

            // lưu postResponse vào Redis
            if (!postList.isEmpty()) {
                postList.forEach(post -> {
                    try {
                        String postJson = objectMapper.writeValueAsString(post);
                        redisTemplate.opsForList().leftPush(redisKey, postJson);
                    } catch (Exception e) {
                        log.error("Error saving post to Redis: {}", post, e);
                    }
                });
                redisTemplate.opsForList().trim(redisKey, 0, 20);
                redisTemplate.expire(redisKey, TTL_SECONDS, TimeUnit.SECONDS);
            }

            return PageResponse.<PostResponse>builder()
                    .currentPage(page)
                    .pageSize(pageData.getSize())
                    .totalPages(pageData.getTotalPages())
                    .totalElements(pageData.getTotalElements())
                    .data(postList)
                    .build();
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.NOT_FETCH_FEED);
        }
    }
}
