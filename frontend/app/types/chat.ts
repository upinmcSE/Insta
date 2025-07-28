import type { ApiResponse } from "./api";
import type { Conversation, Message } from "./types";

export type ConversationResponse = ApiResponse<Conversation>

export type ListConversationResponse = ApiResponse<Conversation[]>

export type ChatMessageResponse = ApiResponse<Message>

export type ListChatMessageResponse = ApiResponse<Message[]>