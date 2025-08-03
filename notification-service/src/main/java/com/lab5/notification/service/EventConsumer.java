package com.lab5.notification.service;

import com.lab5.notification.config.RabbitMQConfig;
import com.lab5.notification.domain.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public EventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEvent(BaseEvent event) {
        try {
            logger.info("Received event: {} - Type: {}", event.getEventId(), event.getEventType());
            
            // Process the event
            notificationService.processEvent(event);
            
            logger.info("Successfully processed event: {}", event.getEventId());
            
        } catch (Exception e) {
            logger.error("Error processing event: {}", event.getEventId(), e);
            // In a real implementation, you might want to send to a dead letter queue
            // or implement retry logic
            throw new RuntimeException("Failed to process event", e);
        }
    }
} 