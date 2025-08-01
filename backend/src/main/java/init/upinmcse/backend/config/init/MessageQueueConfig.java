package init.upinmcse.backend.config.init;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE = "post.exchange";

    public static final String ROUTING_KEY_POST_CREATED = "post.created";
    public static final String ROUTING_KEY_POST_LIKE = "post.like";
    public static final String ROUTING_KEY_POST_COMMENT = "post.comment";

    public static final String POST_FEED_QUEUE = "post.feed.queue";
    public static final String POST_NOTIFICATION_QUEUE = "post.notification.queue";
    public static final String POST_FOLLOWERS_FEED_QUEUE = "post.followers-feed.queue";

    public static final String POST_LIKE_QUEUE = "post.like.queue";
    public static final String POST_COMMENT_QUEUE = "post.comment.queue";

    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange(EXCHANGE);
    }

//    @Bean
//    public Queue postFeedQueue() {
//        return new Queue(POST_FEED_QUEUE);
//    }

//    @Bean
//    public Queue postNotificationQueue() {
//        return new Queue(POST_NOTIFICATION_QUEUE);
//    }

//    @Bean
//    public Binding feedBinding() {
//        return BindingBuilder.bind(postFeedQueue()).to(postExchange()).with(ROUTING_KEY);
//    }

//    @Bean
//    public Binding notificationBinding() {
//        return BindingBuilder.bind(postNotificationQueue()).to(postExchange()).with(ROUTING_KEY);
//    }

    @Bean
    public Queue postFollowersFeedQueue() {
        return new Queue(POST_FOLLOWERS_FEED_QUEUE);
    }

    @Bean
    public Binding followersFeedBinding() {
        return BindingBuilder.bind(postFollowersFeedQueue()).to(postExchange()).with(ROUTING_KEY_POST_CREATED);
    }

    @Bean
    public Queue postLikeQueue() {
        return new Queue(POST_LIKE_QUEUE);
    }

    @Bean Binding postLikeBinding() {
        return BindingBuilder.bind(postLikeQueue()).to(postExchange()).with(ROUTING_KEY_POST_LIKE);
    }

    @Bean Queue postCommentQueue() {
        return new Queue(POST_COMMENT_QUEUE);
    }

    @Bean
    public Binding postCommentBinding() {
        return BindingBuilder.bind(postCommentQueue()).to(postExchange()).with(ROUTING_KEY_POST_COMMENT);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
