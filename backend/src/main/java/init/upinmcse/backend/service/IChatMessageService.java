package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.request.ChatMessageRequest;
import init.upinmcse.backend.dto.response.ChatMessageResponse;

import java.util.List;

public interface IChatMessageService {
    List<ChatMessageResponse> getMessages(String conversationId);
    ChatMessageResponse sendMessage(ChatMessageRequest request);
}
