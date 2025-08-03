package com.lab5.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String CART_EVENTS_EXCHANGE = "cart.events";
    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String INVENTORY_EVENTS_EXCHANGE = "inventory.events";
    public static final String SAGA_EVENTS_EXCHANGE = "saga.events";

    // Queue names for notification service
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // Routing keys
    public static final String CART_ARTICLE_ADDED = "cart.article.added";
    public static final String CART_CLEARED = "cart.cleared";
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String CART_EXPIRED = "cart.expired";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rawMessageListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // Use SimpleMessageConverter to avoid Jackson deserialization issues
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }

    // Notification Queue
    // Exchange declarations
    @Bean
    public TopicExchange cartEventsExchange() {
        return new TopicExchange(CART_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange inventoryEventsExchange() {
        return new TopicExchange(INVENTORY_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange sagaEventsExchange() {
        return new TopicExchange(SAGA_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // Bindings for notification service
    @Bean
    public Binding notificationCartArticleAddedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(new TopicExchange(CART_EVENTS_EXCHANGE))
                .with(CART_ARTICLE_ADDED);
    }

    @Bean
    public Binding notificationCartClearedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(new TopicExchange(CART_EVENTS_EXCHANGE))
                .with(CART_CLEARED);
    }

    @Bean
    public Binding notificationCartExpiredBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(new TopicExchange(CART_EVENTS_EXCHANGE))
                .with(CART_EXPIRED);
    }

    @Bean
    public Binding notificationOrderCreatedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(new TopicExchange(ORDER_EVENTS_EXCHANGE))
                .with(ORDER_CREATED);
    }

    @Bean
    public Binding notificationOrderConfirmedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(new TopicExchange(ORDER_EVENTS_EXCHANGE))
                .with(ORDER_CONFIRMED);
    }
} 