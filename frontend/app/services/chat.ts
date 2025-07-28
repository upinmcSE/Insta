import type { ChatMessageResponse, ConversationResponse, ListChatMessageResponse, ListConversationResponse } from "@/types/chat";
import { Api } from "./api";
import type { Message } from "@/types/types";

async function createConversation(creatorId: string, targetId: string): Promise<ConversationResponse>{
    return Api.post("/chats/conversations/create", {creatorId: creatorId, targetId: targetId})
}

async function getConversations(userId: string): Promise<ListConversationResponse>{
    return Api.get(`/chats/conversations/${userId}`)
}

async function createMessage(conversationId: string, message: string): Promise<ChatMessageResponse>{
    return Api.post("/chats/messages/create", {conversationId: conversationId, message: message})
}

async function getMessages(conversationId: string): Promise<ListChatMessageResponse>{
    return Api.get(`/chats/messages/${conversationId}`)
}

export {
    createConversation,
    getConversations,
    createMessage,
    getMessages
}

