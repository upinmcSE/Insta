package init.upinmcse.backend.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.dto.common.UserInfo;
import init.upinmcse.backend.dto.request.ChatMessageRequest;
import init.upinmcse.backend.dto.response.ChatMessageResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.ChatMessage;
import init.upinmcse.backend.model.Conversation;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.model.WebSocketSession;
import init.upinmcse.backend.repository.db.ChatMessageRepository;
import init.upinmcse.backend.repository.db.ConversationRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import init.upinmcse.backend.repository.db.WebSocketSessionRepository;
import init.upinmcse.backend.service.IChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService implements IChatMessageService {
    ChatMessageRepository chatMessageRepository;
    ConversationRepository conversationRepository;
    UserRepository userRepository;
    SocketIOServer socketIOServer;
    WebSocketSessionRepository webSocketSessionRepository;
    ObjectMapper objectMapper;

    @Override
    public List<ChatMessageResponse> getMessages(String conversationId) {
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ErrorException(ErrorCode.CONVERSATION_NOT_FOUND));

        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedAtDesc(conversationId);

        return messages.stream().map(this::toChatMessageResponse).toList();

    }

    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ErrorException(ErrorCode.CONVERSATION_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .message(request.getMessage())
                .sender(user)
                .conversation(conversation)
                .build();
        chatMessage = chatMessageRepository.save(chatMessage);

        List<String> userIds = conversation.getParticipants().stream()
                .map(User::getId)
                .toList();

        Map<String, WebSocketSession> webSocketSessions =
                webSocketSessionRepository
                        .findAllByUserIdIn(userIds).stream()
                        .collect(Collectors.toMap(
                                WebSocketSession::getSocketSessionId,
                                Function.identity()));

        // Publish socket event to clients
        ChatMessageResponse chatMessageResponse = toChatMessageResponse(chatMessage);
        socketIOServer.getAllClients().forEach(client -> {
            var webSocketSession = webSocketSessions.get(client.getSessionId().toString());

            if (Objects.nonNull(webSocketSession)) {
                String message = null;
                try {
                    chatMessageResponse.setSender(webSocketSession.getUserId().equals(userId));
                    message = objectMapper.writeValueAsString(chatMessageResponse);
                    client.sendEvent("message", message);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return toChatMessageResponse(chatMessage);
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .conversationId(chatMessage.getConversation().getId())
                .createdAt(chatMessage.getCreatedAt())
                .senderInfo(UserInfo.builder()
                        .id(chatMessage.getSender().getId())
                        .fullName(chatMessage.getSender().getFullName())
                        .avatarUrl(chatMessage.getSender().getAvtUrl())
                        .build())
                .build();

        chatMessageResponse.setSender(chatMessage.getSender().getId().equals(userId));

        return chatMessageResponse;
    }
}
