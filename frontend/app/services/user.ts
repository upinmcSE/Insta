import type { User } from "@/types/types";
import { Api } from "./api";
import type { SearchUserResponse, UserResponse } from "@/types/user";
import type { ApiResponse } from "@/types/api";

async function getMyProfile(): Promise<UserResponse> {
    return Api.get("/users/me")
}

async function getUserProfile(userId: string): Promise<UserResponse>{
    return Api.get(`/users/profile/${userId}`)
}

async function search(keywork: string): Promise<SearchUserResponse>{
    return Api.post("users/search", {keywork: keywork})
}

async function follow(userId: string): Promise<void>{
    Api.post(`/users/follow/${userId}`)
}

async function unfollow(userId: string): Promise<void>{
    Api.post(`/users/unfollow/${userId}`)
}

async function listFollowing(userId: string): Promise<UserResponse>{
    return Api.get(`users/following/${userId}`)
}

async function listFollowers(userId: string): Promise<UserResponse>{
    return Api.get(`users/followers/${userId}`)
}

export {
    getMyProfile,
    getUserProfile,
    search,
    follow,
    unfollow,
    listFollowing,
    listFollowers
}