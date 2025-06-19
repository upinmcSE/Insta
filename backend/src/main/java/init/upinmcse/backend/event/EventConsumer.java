package init.upinmcse.backend.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.config.init.MessageQueueConfig;
import init.upinmcse.backend.dto.event.AfterCreatedPostEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EventConsumer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventConsumer {
    ObjectMapper objectMapper;

    @RabbitListener(queues = MessageQueueConfig.MAIN_EVENT_QUEUE)
    public void handleEvent(EventMessage eventMessage) {
        try {
            log.info("Processing event: {} with ID: {}", eventMessage.getEventType(), eventMessage.getEventType());

            switch (eventMessage.getEventType()) {
                case UPDATE_FEED:
                    handleUpdateFeed(eventMessage);
                    break;
                case CREATE_NOTIFICATION:
                    handleCreateNotification(eventMessage);
                    break;
                default:
                    log.warn("Unknown event type: {}", eventMessage.getEventType());
            }

            log.info("Successfully processed event: {} with ID: {}",
                    eventMessage.getEventType(), eventMessage.getEventType());
        } catch (Exception e) {
            log.error("Error processing event: {} with ID: {} - Error: {}",
                    eventMessage.getEventType(), eventMessage.getEventType(), e.getMessage(), e);
            throw e; // Để RabbitMQ có thể retry
        }
    }

    private void handleUpdateFeed(EventMessage eventMessage) {
        try {
            AfterCreatedPostEvent postData = (AfterCreatedPostEvent) convertToPostCreatedMessage(eventMessage.getData());
            //feedService.addPostToUsersFeed(postData);
            //log.debug("Updated feed for post: {}", postData.getPostId());
        } catch (Exception e) {
            log.error("Error updating feed: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void handleCreateNotification(EventMessage eventMessage) {
        try {
            AfterCreatedPostEvent postData = (AfterCreatedPostEvent) convertToPostCreatedMessage(eventMessage.getData());
            //notificationService.createNotificationForFollowers(postData);
            //log.debug("Created notifications for post: {}", postData.getPostId());
        } catch (Exception e) {
            log.error("Error creating notification: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Object convertToPostCreatedMessage(Object data) {
        try {
            if (data instanceof AfterCreatedPostEvent) {
                return data;
            }
            // Convert từ LinkedHashMap (do JSON deserialization) về PostCreatedMessage
            return objectMapper.convertValue(data, AfterCreatedPostEvent.class);
        } catch (Exception e) {
            log.error("Error converting data to PostCreatedMessage: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert event data", e);
        }
    }
}
