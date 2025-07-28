package init.upinmcse.backend.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.nimbusds.jose.JOSEException;
import init.upinmcse.backend.model.WebSocketSession;
import init.upinmcse.backend.service.IJwtService;
import init.upinmcse.backend.service.IWebSocketSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Slf4j(topic = "SocketHandler")
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;
    IJwtService jwtService;
    IWebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(SocketIOClient client) throws ParseException, JOSEException {
        String token = client.getHandshakeData().getSingleUrlParam("token");

        var isValid = jwtService.validateToken(token, false);

        if(Objects.isNull(isValid)){
            log.error("Authentication failed: {}", token);
            client.disconnect();
        }
    }

    @OnEvent("register-session")
    public void onRegisterSession(SocketIOClient client, RegisterSessionData data) throws ParseException {
        String clientSessionId = data.getClientSessionId();

        log.info("Client session registered: {}", clientSessionId);

        if(!Objects.isNull(clientSessionId)){
            // Lưu thông tin phiên làm việc của client
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(jwtService.extractUserId(client.getHandshakeData().getSingleUrlParam("token")))
                    .createdAt(new Date())
                    .build();
            webSocketSession = webSocketSessionService.create(webSocketSession);

            log.info("WebSocketSession created with id: {}", webSocketSession.getId());
        } else {
            log.warn("Client session ID is null or empty");
            client.disconnect();
        }

        // Xác nhận với client
        client.sendEvent("session-confirmed", clientSessionId);
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        webSocketSessionService.delete(client.getSessionId().toString());
        log.info("Client disConnected: {}", client.getSessionId());
    }

    /**
     * @PostConstruct is used to start the server after the bean is created.
     */
    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started");
    }

    /**
     * @PreDestroy is used to stop the server before the bean is destroyed.
     */
    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }

    public static class RegisterSessionData {
        private String clientSessionId;

        public String getClientSessionId() {
            return clientSessionId;
        }
        public void setClientSessionId(String clientSessionId) {
            this.clientSessionId = clientSessionId;
        }

    }
}


