import type { ApiResponse } from "./api";
import type { User } from "./types";

export type LoginResponse = ApiResponse<
    {
        accessToken:string, 
        refreshToken: string,
        userLoginInfo: User
    }
>

export type RegisterResponse = ApiResponse<{
    
}>
