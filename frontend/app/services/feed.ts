import { Api } from "./api";
import type { FeedResponse } from "@/types/feed";

async function dynamicFeed(): Promise<FeedResponse> {
    return Api.get("/feeds/dynamic")
}

async function precomputedFeed(): Promise<FeedResponse> {
    return Api.get("/feeds/precomputed")
}

export {
    dynamicFeed,
    precomputedFeed
}