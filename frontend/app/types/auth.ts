import type { ApiResponse } from "./api";

export type LoginResponse = ApiResponse<
    {accessToken:string, refreshToken: string}
>

export type RegisterResponse = ApiResponse<{
    
}>