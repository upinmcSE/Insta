package init.upinmcse.backend.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.config.init.MessageQueueConfig;
import init.upinmcse.backend.dto.event.AfterCreatedPostEvent;
import init.upinmcse.backend.enums.EventType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EventProducer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventProducer {
    RabbitTemplate rabbitTemplate;
    ObjectMapper objectMapper;

    public void publishEvent(EventType eventType, Object data) throws JsonProcessingException {
        try {
            Object msg = objectMapper.writeValueAsString(data);
            EventMessage eventMessage = EventMessage.builder()
                    .eventType(eventType)
                    .data(msg)
                    .build();
            rabbitTemplate.convertAndSend(MessageQueueConfig.MAIN_EVENT_QUEUE, eventMessage);
            log.info("Published event: {} with ID: {}", eventType, eventMessage.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish event: {} - Error: {}", eventType, e.getMessage(), e);
            throw e;
        }
    }

    public void publishUpdateFeedEvent(AfterCreatedPostEvent postData) throws JsonProcessingException {
        publishEvent(EventType.UPDATE_FEED, postData);
    }

    public void publishCreateNotificationEvent(AfterCreatedPostEvent postData) throws JsonProcessingException {
        publishEvent(EventType.CREATE_NOTIFICATION, postData);
    }

    // Publish multiple events for one action
    public void publishPostCreatedEvents(AfterCreatedPostEvent postData) throws JsonProcessingException {
        publishUpdateFeedEvent(postData);
        publishCreateNotificationEvent(postData);
    }
}
