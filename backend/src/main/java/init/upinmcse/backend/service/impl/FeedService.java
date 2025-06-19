package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;
import init.upinmcse.backend.enums.Status;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.repository.FileRepository;
import init.upinmcse.backend.repository.PostLikeRepository;
import init.upinmcse.backend.repository.PostRepostitory;
import init.upinmcse.backend.service.IFeedService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService implements IFeedService {
    PostRepostitory postRepostitory;
    FileRepository fileRepository;
    PostLikeRepository postLikeRepository;

    @Override
    public PageResponse<PostResponse> getGlobalFeed(int page, int size) {

        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = postRepostitory.findAllByStatus(Status.ACTIVE, pageable);

        var postList = pageData.getContent().stream().map(
                post -> PostResponse.builder()
                        .postId(post.getId())
                        .userId(post.getUser().getId())
                        .fullName(post.getUser().getUserProfile().getFullName())
                        .avtUrl(post.getUser().getUserProfile().getAvtUrl())
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
    public PageResponse<PostResponse> getFollowingFeed(int page, int size, String userId) {
        return null;
    }
}
