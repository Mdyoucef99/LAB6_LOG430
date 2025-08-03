package com.lab5.notification.service;

import com.lab5.notification.config.RabbitMQConfig;
import com.lab5.notification.domain.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.amqp.core.Message;

@Service
public class EventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public EventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE, containerFactory = "rawMessageListenerContainerFactory")
    public void handleEvent(Message message) {
        try {
            // Extract JSON from message body (with charset)
            String eventJson = new String(message.getBody(), StandardCharsets.UTF_8);
            logger.info("Received raw event JSON: {}", eventJson);
    
            // Parse JSON directly to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> eventMap = objectMapper.readValue(eventJson, Map.class);
    
            // Create a BaseEvent manually from the map
            BaseEvent baseEvent = createBaseEventFromMap(eventMap);
            logger.info("Received event: {} - Type: {}", baseEvent.getEventId(), baseEvent.getEventType());
    
            // Process the event
            notificationService.processEvent(baseEvent);
            logger.info("Successfully processed event: {}", baseEvent.getEventId());
    
        } catch (Exception e) {
            logger.error("Error processing event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process event", e);
        }
    }
    
    private BaseEvent createBaseEventFromMap(Map<String, Object> eventMap) {
        try {
            
            BaseEvent baseEvent = new BaseEvent();
            baseEvent.setEventId((String) eventMap.get("eventId"));
            baseEvent.setEventType((String) eventMap.get("eventType"));
            baseEvent.setAggregateId((String) eventMap.get("aggregateId"));
            baseEvent.setAggregateType((String) eventMap.get("aggregateType"));
            baseEvent.setSagaId((String) eventMap.get("sagaId"));
            baseEvent.setVersion((Integer) eventMap.get("version"));
            
            // Handle numeric fields that might be primitives
            Object cartId = eventMap.get("cartId");
            if (cartId != null) {
                baseEvent.setCartId(cartId instanceof Integer ? (Integer) cartId : Integer.valueOf(cartId.toString()));
            }
            
            Object productId = eventMap.get("productId");
            if (productId != null) {
                baseEvent.setProductId(productId instanceof Integer ? (Integer) productId : Integer.valueOf(productId.toString()));
            }
            
            Object quantity = eventMap.get("quantity");
            if (quantity != null) {
                baseEvent.setQuantity(quantity instanceof Integer ? (Integer) quantity : Integer.valueOf(quantity.toString()));
            }
            
            Object price = eventMap.get("price");
            if (price != null) {
                baseEvent.setPrice(price instanceof Double ? (Double) price : Double.valueOf(price.toString()));
            }
            
            Object customerId = eventMap.get("customerId");
            if (customerId != null) {
                baseEvent.setCustomerId(customerId instanceof Integer ? (Integer) customerId : Integer.valueOf(customerId.toString()));
            }
            
            Object orderId = eventMap.get("orderId");
            if (orderId != null) {
                baseEvent.setOrderId(orderId instanceof Integer ? (Integer) orderId : Integer.valueOf(orderId.toString()));
            }
            
            Object totalAmount = eventMap.get("totalAmount");
            if (totalAmount != null) {
                baseEvent.setTotalAmount(totalAmount instanceof Double ? (Double) totalAmount : Double.valueOf(totalAmount.toString()));
            }
            
            baseEvent.setProductName((String) eventMap.get("productName"));
            
            // Handle timestamp
            Object timestamp = eventMap.get("timestamp");
            if (timestamp != null) {
                if (timestamp instanceof String) {
                    // Parse ISO timestamp with timezone and convert to LocalDateTime
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse((String) timestamp);
                    baseEvent.setTimestamp(offsetDateTime.toLocalDateTime());
                } else if (timestamp instanceof LocalDateTime) {
                    baseEvent.setTimestamp((LocalDateTime) timestamp);
                }
            }
            
            return baseEvent;
            
        } catch (Exception e) {
            logger.error("Error creating BaseEvent from event map: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create BaseEvent", e);
        }
    }
} 