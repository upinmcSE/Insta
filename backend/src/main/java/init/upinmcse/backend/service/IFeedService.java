package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;

public interface IFeedService {
    PageResponse<PostResponse> getGlobalFeed(int page, int size);
    PageResponse<PostResponse> getFollowingFeed(int page, int size, String userId);
}
