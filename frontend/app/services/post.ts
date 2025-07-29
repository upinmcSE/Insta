import type { FeedResponse } from "@/types/feed";
import { Api } from "./api";
import type { PostResponse } from "@/types/post";

async function createPost(images: File[], caption: string): Promise<PostResponse>{
    const formData = new FormData();

    images.forEach((image) => {
        formData.append(`file`, image);
    });

    formData.append('caption', caption)

    return Api.post("/posts", formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

async function like(postId: number): Promise<void>{
    Api.post(`/posts/like/${postId}`);
}

async function unlike(postId: number): Promise<void>{
    Api.post(`/posts/unlike/${postId}`);
}

async function getPostsByUser(userId: string): Promise<FeedResponse>{
    return Api.get(`/posts/user/${userId}`)
}

export {
    createPost,
    like,
    unlike,
    getPostsByUser
}
