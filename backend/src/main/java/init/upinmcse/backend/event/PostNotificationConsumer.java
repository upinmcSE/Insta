package init.upinmcse.backend.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//@RabbitListener(queues = "post.notification.queue")
public class PostNotificationConsumer {
}
