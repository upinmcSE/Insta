package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.common.PageResponse;
import init.upinmcse.backend.dto.request.PostRequest;
import init.upinmcse.backend.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPostService {
    PostResponse createPost(List<MultipartFile> files, PostRequest postRequest);
    PostResponse getPostById(Long postId);
    PageResponse<PostResponse> getPostsByUserId(String userId, int page, int size);
    PageResponse<PostResponse> getNewPosts(int page, int size);
    void deletePost(Long postId);
    PostResponse updatePost(Long postId, PostRequest postRequest);
    void likePost(Long postId);
    void unlikePost(Long postId);
}
