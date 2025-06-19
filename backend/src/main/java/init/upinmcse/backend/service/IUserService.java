package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.ChangePasswordRequest;
import init.upinmcse.backend.dto.request.UpdateInfo;
import init.upinmcse.backend.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    UserResponse getMe();
    UserResponse getProfile(String userId);
    UserResponse updateInfo(UpdateInfo request);
    String changeAvatar(MultipartFile file);
    void changePassword(ChangePasswordRequest request);
    void followUser(String userId);
    void unfollowUser(String userId);
    PageResponse<UserResponse> searchUser(String query, int page, int size);
    List<UserResponse> getFollowers(String userId);
    List<UserResponse> getFollowing(String userId);
}
