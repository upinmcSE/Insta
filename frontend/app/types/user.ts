import type { ApiResponse } from "./api";
import type { User, UserProfile } from "./types";

export type UserResponse = ApiResponse<UserProfile>

export type SearchUserResponse = ApiResponse<User[]>