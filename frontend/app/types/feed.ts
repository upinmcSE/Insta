import type { ApiResponse } from "./api";
import type { Post } from "./types";

export type FeedResponse = ApiResponse<
    {
        currentPage: number,
        totalPages: number,
        pageSize: number,
        totalElements: number,
        data: Post[]
    }
>
