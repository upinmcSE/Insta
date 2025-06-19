package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.ChangePasswordRequest;
import init.upinmcse.backend.dto.request.UpdateInfo;
import init.upinmcse.backend.dto.response.UserResponse;
import init.upinmcse.backend.enums.FileType;
import init.upinmcse.backend.enums.GENDER;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.model.UserFollowing;
import init.upinmcse.backend.repository.FileRepository;
import init.upinmcse.backend.repository.FollowerRepository;
import init.upinmcse.backend.repository.UserRepository;
import init.upinmcse.backend.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    UserRepository userRepository;
    FileRepository fileRepository;
    FollowerRepository followerRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        return UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getUserProfile().getFullName())
                        .bio(user.getUserProfile().getBio())
                        .dob(user.getUserProfile().getDob())
                        .gender(user.getUserProfile().getGender())
                        .avtUrl(getUserImage(user.getId()))
                .followers(followerRepository.findFollowingUserIdsByFollowerUserId(user.getId()))
                .following(followerRepository.findFollowerUserIdsByFollowingUserId(user.getId()))
                        .build();
    }

    @Override
    public UserResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getUserProfile().getFullName())
                .bio(user.getUserProfile().getBio())
                .dob(user.getUserProfile().getDob())
                .gender(user.getUserProfile().getGender())
                .avtUrl(getUserImage(user.getId()))
                .followers(followerRepository.findFollowingUserIdsByFollowerUserId(user.getId()))
                .following(followerRepository.findFollowerUserIdsByFollowingUserId(user.getId()))
                .build();
    }

    @Transactional
    @Override
    public UserResponse updateInfo(UpdateInfo request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
//        user.setFullName(request.getFullName());
//        user.setBio(request.getBio());
//        user.setDob(request.getDob());
//        user.setGender(GENDER.valueOf(request.getGender().toUpperCase()));
//        userRepository.saveAndFlush(user);
//
//        return UserResponse.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .fullName(user.getFullName())
//                .bio(user.getBio())
//                .dob(user.getDob())
//                .gender(user.getGender())
//                .avtUrl(getUserImages(user.getId()).get(0))
//                .profileUrl(getUserImages(user.getId()).get(1))
//                .followers(followerRepository.findFollowingUserIdsByFollowerUserId(user.getId()))
//                .following(followerRepository.findFollowerUserIdsByFollowingUserId(user.getId()))
//                .build();
        return null;
    }

    @Override
    public String changeAvatar(MultipartFile file) {
        return null;
    }


    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (request.getConfirmPassword().equals(request.getNewPassword())) {
            throw new ErrorException(ErrorCode.INVALID_PASSWORD);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ErrorException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(request.getNewPassword());
        userRepository.saveAndFlush(user);
    }

    @Transactional
    @Override
    public void followUser(String userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        // kiểm tra người dùng có tồn tại không
        if( !userRepository.existsById(userId)) {
            throw new ErrorException(ErrorCode.NOT_FOUND_USER);
        }

        // Kiểm tra xem người dùng đã theo dõi người dùng khác chưa
        if (followerRepository.existsByFollowerUserIdAndFollowingUserId(user.getId(), userId)) {
            throw new ErrorException(ErrorCode.ALREADY_FOLLOWING);
        }

        UserFollowing following = new UserFollowing();
        following.setFollowerUserId(user.getId());
        following.setFollowingUserId(userId);
        followerRepository.save(following);
    }

    @Transactional
    @Override
    public void unfollowUser(String userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        // kiểm tra người dùng có tồn tại không
        if( !userRepository.existsById(userId)) {
            throw new ErrorException(ErrorCode.NOT_FOUND_USER);
        }

        // Kiểm tra xem người dùng có đang theo dõi người dùng khác không
        if (!followerRepository.existsByFollowerUserIdAndFollowingUserId(user.getId(), userId)) {
            throw new ErrorException(ErrorCode.NOT_FOLLOWING);
        }

        // Xóa mối quan hệ theo dõi
        UserFollowing following = followerRepository.findByFollowerUserIdAndFollowingUserId(user.getId(), userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOLLOWING));
        followerRepository.delete(following);

    }

    @Override
    public PageResponse<UserResponse> searchUser(String query, int page, int size) {

        return null;
    }

    @Override
    public List<UserResponse> getFollowers(String userId) {
//       User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
//
//        List<UserFollowing> followers = followerRepository.findByFollowingUserId(user.getId());
//
//        return followers.stream()
//                .map(follower -> {
//                    User followedUser = userRepository.findById(follower.getFollowerUserId())
//                            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
//                    return UserResponse.builder()
//                            .id(followedUser.getId())
//                            .avtUrl(getUserImages(followedUser.getId()).get(0))
//                            .fullName(followedUser.getFullName())
//                            .build();
//                })
//                .toList();
        return  null;
    }

    @Override
    public List<UserResponse> getFollowing(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

//        List<UserFollowing> following = followerRepository.findByFollowerUserId(user.getId());
//
//
//        return following.stream()
//                .map(followed -> {
//                    User followedUser = userRepository.findById(followed.getFollowingUserId())
//                            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
//                    return UserResponse.builder()
//                            .id(followedUser.getId())
//                            .avtUrl(getUserImages(followedUser.getId()).get(0))
//                            .fullName(followedUser.getFullName())
//                            .build();
//                })
//                .toList();
        return  null;
    }

    private String getUserImage(String userId){

        return Optional.ofNullable(
                fileRepository.findByFileTypeAndUserId(FileType.USER_AVATAR, userId)
        ).map(File::getPath).orElse(null); // hoặc orElse("default-avatar.png")
    }
}
