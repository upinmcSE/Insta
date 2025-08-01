package init.upinmcse.backend.event;

import init.upinmcse.backend.config.init.MessageQueueConfig;
import init.upinmcse.backend.constant.NotificationType;
import init.upinmcse.backend.dto.event.LikePostEvent;
import init.upinmcse.backend.model.Notification;
import init.upinmcse.backend.repository.db.NotificationRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikePostConsumer {
    NotificationRepository notificationRepository;
    UserRepository userRepository;

    @RabbitListener(queues = MessageQueueConfig.POST_LIKE_QUEUE)
    public void pushNotification(LikePostEvent event) {
        var sender = userRepository.findById(event.getSenderId()).orElseThrow(
                () -> new RuntimeException("User not found"));

        var receiver = userRepository.findById(event.getReceiverId()).orElseThrow(
                () -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .content(sender.getFullName()+ " đã thích bài viết của bạn!")
                .fromUser(sender)
                .toUser(receiver)
                .isRead(false)
                .notificationType(NotificationType.LIKE)
                .build();

        notificationRepository.save(notification);
    }
}
