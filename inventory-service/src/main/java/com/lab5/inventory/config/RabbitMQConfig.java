package com.lab5.inventory.config;

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
    public static final String SAGA_EVENTS_EXCHANGE = "saga.events";

    // Queue names
    public static final String INVENTORY_QUEUE = "inventory.queue";

    // Routing keys
    public static final String CART_VALIDATED = "cart.validated";
    public static final String STOCK_RESERVED = "stock.reserved";
    public static final String STOCK_RESERVATION_FAILED = "stock.reservation.failed";

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

    // Saga Events Exchange
    @Bean
    public TopicExchange sagaEventsExchange() {
        return new TopicExchange(SAGA_EVENTS_EXCHANGE);
    }

    // Inventory Queue for saga events
    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_QUEUE, true);
    }

    // Saga Event Bindings
    @Bean
    public Binding cartValidatedBinding() {
        return BindingBuilder.bind(inventoryQueue())
                .to(sagaEventsExchange())
                .with(CART_VALIDATED);
    }
} 