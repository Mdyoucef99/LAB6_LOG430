package com.lab5.cart.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String CART_EVENTS_EXCHANGE = "cart.events";
    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String INVENTORY_EVENTS_EXCHANGE = "inventory.events";
    public static final String SAGA_EVENTS_EXCHANGE = "saga.events";

    // Queue names
    public static final String CART_EVENTS_QUEUE = "cart.events.queue";
    public static final String ORDER_EVENTS_QUEUE = "order.events.queue";
    public static final String INVENTORY_EVENTS_QUEUE = "inventory.events.queue";
    public static final String SAGA_EVENTS_QUEUE = "saga.events.queue";

    // Routing keys
    public static final String CART_ARTICLE_ADDED = "cart.article.added";
    public static final String CART_ARTICLE_REMOVED = "cart.article.removed";
    public static final String CART_MODIFIED = "cart.modified";
    public static final String CART_EXPIRED = "cart.expired";
    public static final String CART_CLEARED = "cart.cleared";

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

    // Cart Events Exchange
    @Bean
    public TopicExchange cartEventsExchange() {
        return new TopicExchange(CART_EVENTS_EXCHANGE);
    }

    // Order Events Exchange
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EVENTS_EXCHANGE);
    }

    // Inventory Events Exchange
    @Bean
    public TopicExchange inventoryEventsExchange() {
        return new TopicExchange(INVENTORY_EVENTS_EXCHANGE);
    }

    // Saga Events Exchange
    @Bean
    public TopicExchange sagaEventsExchange() {
        return new TopicExchange(SAGA_EVENTS_EXCHANGE);
    }

    // Cart Events Queue
    @Bean
    public Queue cartEventsQueue() {
        return new Queue(CART_EVENTS_QUEUE, true);
    }

    // Order Events Queue
    @Bean
    public Queue orderEventsQueue() {
        return new Queue(ORDER_EVENTS_QUEUE, true);
    }

    // Inventory Events Queue
    @Bean
    public Queue inventoryEventsQueue() {
        return new Queue(INVENTORY_EVENTS_QUEUE, true);
    }

    // Saga Events Queue
    @Bean
    public Queue sagaEventsQueue() {
        return new Queue(SAGA_EVENTS_QUEUE, true);
    }

    // Bindings for Cart Events
    @Bean
    public Binding cartArticleAddedBinding() {
        return BindingBuilder.bind(cartEventsQueue())
                .to(cartEventsExchange())
                .with(CART_ARTICLE_ADDED);
    }

    @Bean
    public Binding cartArticleRemovedBinding() {
        return BindingBuilder.bind(cartEventsQueue())
                .to(cartEventsExchange())
                .with(CART_ARTICLE_REMOVED);
    }

    @Bean
    public Binding cartModifiedBinding() {
        return BindingBuilder.bind(cartEventsQueue())
                .to(cartEventsExchange())
                .with(CART_MODIFIED);
    }

    @Bean
    public Binding cartExpiredBinding() {
        return BindingBuilder.bind(cartEventsQueue())
                .to(cartEventsExchange())
                .with(CART_EXPIRED);
    }

    @Bean
    public Binding cartClearedBinding() {
        return BindingBuilder.bind(cartEventsQueue())
                .to(cartEventsExchange())
                .with(CART_CLEARED);
    }
} 