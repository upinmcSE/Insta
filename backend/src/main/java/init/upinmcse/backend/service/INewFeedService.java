package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;

public interface INewFeedService {
    PageResponse<PostResponse> getDynamicPost(int page, int size);
    PageResponse<PostResponse> getPrecomputedPost(int page, int size, String userId);
}
