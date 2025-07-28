package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.model.WebSocketSession;
import init.upinmcse.backend.repository.db.WebSocketSessionRepository;
import init.upinmcse.backend.service.IWebSocketSessionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketSessionService implements IWebSocketSessionService {
    WebSocketSessionRepository webSocketSessionRepository;

    @Override
    @Transactional
    public WebSocketSession create(WebSocketSession webSocketSession) {
        return webSocketSessionRepository.save(webSocketSession);
    }

    @Override
    @Transactional
    public void delete(String sessionId) {
        webSocketSessionRepository.deleteBySocketSessionId(sessionId);
    }
}
