package init.upinmcse.backend.config.init;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String MAIN_EVENT_QUEUE = "main-event-queue";

    @Bean
    Queue mainEventQueue() {
        return QueueBuilder.durable(MAIN_EVENT_QUEUE).build();
    }
}
