package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;
import init.upinmcse.backend.service.IFeedService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {
    IFeedService feedService;

    @GetMapping("/global")
    public BaseResponse<PageResponse<PostResponse>> getGlobalFeed(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return BaseResponse.<PageResponse<PostResponse>>builder()
                .message("Global feed data")
                .result(feedService.getGlobalFeed(page, size))
                .build();
    }

    @GetMapping("/following")
    public BaseResponse<PageResponse<PostResponse>> getFollowingFeed(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "userId") String userId
    ) {
        return BaseResponse.<PageResponse<PostResponse>>builder()
                .message("Following feed data")
                .result(new PageResponse<>())
                .build();
    }
}
