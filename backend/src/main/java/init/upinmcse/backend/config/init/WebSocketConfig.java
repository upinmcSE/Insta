package init.upinmcse.backend.config.init;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Client sẽ nhận tin nhắn từ server thông qua /topic
        registry.setApplicationDestinationPrefixes("/app"); // Client sẽ gửi tin nhắn đến server thông qua /app
//        registry.setUserDestinationPrefix("/user"); // Tiền tố cho các điểm đến của người dùng
    }

    // Cấu hình kết nối từ client đến server
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Client sẽ kết nối đến endpoint này
                .setAllowedOriginPatterns("*") // Cho phép tất cả các nguồn gốc
                .withSockJS(); // Sử dụng SockJS để hỗ trợ các trình duyệt không hỗ trợ WebSocket

        registry.addEndpoint("/poops/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
