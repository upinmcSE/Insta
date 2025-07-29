export interface User {
    id: string;
    fullName: string;
    avatarUrl?: string;
}

export interface UserProfile {
    id: string;
    fullName: string;
    email: string;
    avtUrl: string;
    bio: string;
    followers: string[];
    following: string[];
}

export interface Post {
    postId: number;
    userId: string;
    fullName: string;
    avatarUrl: string;
    fileUrls: string[];
    caption: string;
    likedUserIds: string[];
    createdAt: string; // Changed to number timestamp
}

export interface ReplyComment {
    replyid: number;
    commentId: number;
    userId: string;
    fullName: string;
    avtUrl: string;
    content: string;
    created_at: number; // Changed to number timestamp
}

export interface Comment {
    commentId: number;
    postId: number;
    userId: string;
    fullName: string;
    avtUrl: string;
    content: string;
    reply_comment: ReplyComment[]
    createdAt: number; // Changed to number timestamp
}

export interface Message {
    id: string;
    conversationId: string;
    sender: boolean;
    message: string;
    senderInfo: User
    createdAt: Date
}

export interface Conversation {
    id: string;
    ownerId: string;
    ownerName: string;
    participant: User;
    createAt: Date;
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