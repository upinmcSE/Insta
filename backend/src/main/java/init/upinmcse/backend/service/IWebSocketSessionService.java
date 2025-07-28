package init.upinmcse.backend.service;

import init.upinmcse.backend.model.WebSocketSession;

public interface IWebSocketSessionService {
    WebSocketSession create(WebSocketSession webSocketSession);
    void delete(String sessionId);
}
