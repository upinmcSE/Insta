package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.request.ConversationRequest;
import init.upinmcse.backend.dto.response.ConversationResponse;

import java.util.List;

public interface IConversationService {
    List<ConversationResponse> myConversations(String userId);
    ConversationResponse create(ConversationRequest conversationRequest);

}
