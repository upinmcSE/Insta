package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.ConversationType;
import init.upinmcse.backend.dto.request.ConversationRequest;
import init.upinmcse.backend.dto.common.UserInfo;
import init.upinmcse.backend.dto.response.ConversationResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.Conversation;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.db.ConversationRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import init.upinmcse.backend.service.IConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService implements IConversationService {

    ConversationRepository conversationRepository;
    UserRepository userRepository;

    @Override
    public List<ConversationResponse> myConversations(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        List<Conversation> conversations = conversationRepository.findAllByParticipant(user);

        if (conversations.isEmpty()) {
            return List.of();
        }

        return conversations.stream()
                .map(conversation -> toConversationResponse(conversation, user))
                .collect(Collectors.toList());
    }

    @Override
    public ConversationResponse create(ConversationRequest conversationRequest) {
        User createUser = userRepository.findById(conversationRequest.getCreatorId())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        User targetUser = userRepository.findById(conversationRequest.getTargetId())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        List<String> userIds = new ArrayList<>();
        userIds.add(createUser.getId());
        userIds.add(targetUser.getId());

        var sortedIds = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIds);

        var conversation = conversationRepository.findByHashConversation(userIdHash)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setType(ConversationType.DIRECT);
                    newConversation.setHashConversation(userIdHash);
                    newConversation.setParticipants(new HashSet<>(List.of(createUser, targetUser)));
                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation, createUser);
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        // SHA 256

        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation, User currentUser) {
        // Tìm participant (user còn lại trong conversation)
        User participant = conversation.getParticipants().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No other participant found in conversation"));

        // Tạo UserLoginInfo cho participant
        UserInfo participantInfo = new UserInfo();
        participantInfo.setId(participant.getId());
        participantInfo.setFullName(participant.getFullName());
        participantInfo.setAvatarUrl(participant.getAvtUrl());

        // Tạo ConversationResponse
        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setOwnerId(currentUser.getId());
        response.setOwnerName(currentUser.getFullName());
        response.setOwnerAvtUrl(currentUser.getAvtUrl());
        response.setParticipant(participantInfo);
        response.setCreatedAt(conversation.getCreatedAt()); // Giả sử BaseEntity có createdAt

        return response;
    }
}
