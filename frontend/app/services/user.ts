import { Api } from "./api";
import type { UserResponse } from "@/types/user";

async function getMyProfile(): Promise<UserResponse> {
    return Api.get("/users/me")
}

async function getUserProfile(userId: string): Promise<UserResponse>{
    return Api.get(`/users/profile/${userId}`)
}

export {
    getMyProfile,
    getUserProfile
}