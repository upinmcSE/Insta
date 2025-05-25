package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.response.PostResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {

    @GetMapping("/global")
    public BaseResponse<PageResponse<PostResponse>> getGlobalFeed() {
        return BaseResponse.<PageResponse<PostResponse>>builder()
                .message("Global feed data")
                .result(new PageResponse<>())
                .build();
    }

    @GetMapping("/following")
    public BaseResponse<PageResponse<PostResponse>> getFollowingFeed() {
        return BaseResponse.<PageResponse<PostResponse>>builder()
                .message("Following feed data")
                .result(new PageResponse<>())
                .build();
    }
}
