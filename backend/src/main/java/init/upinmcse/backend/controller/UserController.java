package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.ChangePasswordRequest;
import init.upinmcse.backend.dto.request.UpdateInfo;
import init.upinmcse.backend.dto.response.UserResponse;
import init.upinmcse.backend.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    IUserService userService;

    @PatchMapping("/change-password")
    public BaseResponse<Void> changePassword(ChangePasswordRequest request) {
        userService.changePassword(request);
        return BaseResponse.<Void>builder()
                .message("Change password successfully")
                .build();
    }

    @GetMapping("/profile/{userId}")
    public BaseResponse<UserResponse> getProfile(@PathVariable String userId) {
        return BaseResponse.<UserResponse>builder()
                .message("Get profile successfully")
                .result(userService.getProfile(userId))
                .build();
    }

    @GetMapping("/me")
    public BaseResponse<UserResponse> getMe() {
        return BaseResponse.<UserResponse>builder()
                .message("Get me successfully")
                .result(userService.getMe())
                .build();
    }

    @PatchMapping("/update-info")
    public BaseResponse<UserResponse> updateInfo(@RequestBody UpdateInfo request) {
        return BaseResponse.<UserResponse>builder()
                .message("Update info successfully")
                .result(userService.updateInfo(request))
                .build();
    }

    @PatchMapping("/change-avatar-image")
    public BaseResponse<String> changeAvatar() {
        return BaseResponse.<String>builder()
                .message("Change avatar successfully")
                .result("Updated user avatar")
                .build();
    }

    @PatchMapping("/change-profile-image")
    public BaseResponse<String> changeProfileImage() {
        return BaseResponse.<String>builder()
                .message("Change profile image successfully")
                .result("Updated user profile image")
                .build();
    }

    @PostMapping("/follow/{userId}")
    public BaseResponse<Void> followUser(@PathVariable String userId) {
        userService.followUser(userId);
        return BaseResponse.<Void>builder()
                .message("Follow user successfully")
                .build();
    }

    @PostMapping("/unfollow/{userId}")
    public BaseResponse<Void> unfollowUser(@PathVariable String userId) {
        userService.unfollowUser(userId);
        return BaseResponse.<Void>builder()
                .message("Unfollow user successfully")
                .build();
    }

    @GetMapping("/search")
    public BaseResponse<PageResponse<UserResponse>> searchUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        return BaseResponse.<PageResponse<UserResponse>>builder()
                .message("Search user successfully")
                .result(userService.searchUser(query, page, size))
                .build();
    }

    @GetMapping("/following/{userId}")
    public BaseResponse<List<UserResponse>> getFollowingUsers(@RequestParam String userId) {
        List<UserResponse> followingUsers = userService.getFollowing(userId);
        return BaseResponse.<List<UserResponse>>builder()
                .message("Get following users successfully")
                .result(followingUsers)
                .build();
    }

    @GetMapping("/followers/{userId}")
    public BaseResponse<List<UserResponse>> getFollowers(@RequestParam String userId) {
        List<UserResponse> followers = userService.getFollowers(userId);
        return BaseResponse.<List<UserResponse>>builder()
                .message("Get followers successfully")
                .result(followers)
                .build();
    }
}
