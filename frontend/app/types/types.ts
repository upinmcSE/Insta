export interface User {
    id: string;
    fullName: string;
    avatarUrl?: string;
}

export interface UserProfile {
    id: string;
    fullname: string;
    email: string;
    avatar_picture: string;
    bio: string;
    follower_count: number;
    following_count: number;
    is_following?: boolean;
}

export interface Post {
    id: string;
    user_id: string;
    images: string[];
    caption: string;
    like_count?: number;
    liked_by_current_user?: boolean;
    comments: Comment[];
    created_at: number; // Changed to number timestamp
    user: User;
}

export interface ReplyComment {
    id: string;
    comment_id: string;
    user_id: string;
    content: string;
    created_at: number; // Changed to number timestamp
    user: User;
}

export interface Comment {
    id: string;
    post_id: string;
    user_id: string;
    content: string;
    reply_comment: ReplyComment[]
    created_at: number; // Changed to number timestamp
    user: User;
}

export interface PostWithDetails {
    id: string;
    user_id: string;
    images: string[];
    caption: string;
    like_count?: number;
    liked_by_current_user?: boolean;
    comments: Comment[];
    created_at: number; // Changed to number timestamp
    user: UserProfile;
}