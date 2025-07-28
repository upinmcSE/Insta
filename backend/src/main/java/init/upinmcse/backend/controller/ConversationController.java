package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.request.ChatMessageRequest;
import init.upinmcse.backend.dto.request.ConversationRequest;
import init.upinmcse.backend.dto.response.ChatMessageResponse;
import init.upinmcse.backend.dto.response.ConversationResponse;
import init.upinmcse.backend.service.IChatMessageService;
import init.upinmcse.backend.service.IConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    IConversationService conversationService;
    IChatMessageService  chatMessageService;

    @PostMapping("/conversations/create")
    public BaseResponse<ConversationResponse> create(@RequestBody ConversationRequest request){
        return BaseResponse.<ConversationResponse>builder()
                .message("Created successful")
                .result(conversationService.create(request))
                .build();
    }

    @GetMapping("/conversations/{userId}")
    public BaseResponse<List<ConversationResponse>> getConversations(@PathVariable String userId){
        return BaseResponse.<List<ConversationResponse>>builder()
                .message("lists conversation")
                .result(conversationService.myConversations(userId))
                .build();
    }

    @PostMapping("/messages/create")
    public BaseResponse<ChatMessageResponse> createMessage(@RequestBody ChatMessageRequest request) {
        return BaseResponse.<ChatMessageResponse>builder()
                .message("Conversation created successfully")
                .result(chatMessageService.sendMessage(request))
                .build();
    }

    @GetMapping("/messages/{conversationId}")
    public BaseResponse<List<ChatMessageResponse>> getMessages(@PathVariable String conversationId){
        return BaseResponse.<List<ChatMessageResponse>>builder()
                .message("Messages retrieved successfully")
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }
}
