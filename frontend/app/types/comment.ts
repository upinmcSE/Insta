import type { ApiResponse } from "./api";
import type { Comment, ReplyComment } from "./types";

export type CommentResponse = ApiResponse<Comment>

export type ReplyCommentResponse = ApiResponse<ReplyComment>