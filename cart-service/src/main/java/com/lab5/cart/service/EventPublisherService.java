package com.lab5.cart.service;

import com.lab5.cart.config.RabbitMQConfig;
import com.lab5.cart.domain.events.CartEvent;
import com.lab5.cart.domain.events.ArticleAjoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishCartEvent(CartEvent event, String routingKey) {
        try {
            logger.info("Publishing cart event: {} with routing key: {}", event.getEventType(), routingKey);
            rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, routingKey, event);
            logger.info("Successfully published cart event: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish cart event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
    
    public void publishArticleAdded(int cartId, int productId, String productName, int quantity, double price, int customerId) {
        ArticleAjoute event = new ArticleAjoute(cartId, productId, productName, quantity, price, customerId);
        publishCartEvent(event, RabbitMQConfig.CART_ARTICLE_ADDED);
    }
    
    public void publishCartModified(int cartId, int customerId) {
        CartModified event = new CartModified(cartId, customerId);
        publishCartEvent(event, RabbitMQConfig.CART_MODIFIED);
    }
    
    public void publishCartCleared(int cartId, int customerId) {
        CartCleared event = new CartCleared(cartId, customerId);
        publishCartEvent(event, RabbitMQConfig.CART_CLEARED);
    }
    
    // Inner classes for specific events
    public static class CartModified extends CartEvent {
        private int cartId;
        private int customerId;
        
        public CartModified() {
            super();
        }
        
        public CartModified(int cartId, int customerId) {
            super("cart-" + cartId, "Cart");
            this.cartId = cartId;
            this.customerId = customerId;
        }
        
        public int getCartId() {
            return cartId;
        }
        
        public void setCartId(int cartId) {
            this.cartId = cartId;
        }
        
        public int getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }
    }
    
    public static class CartCleared extends CartEvent {
        private int cartId;
        private int customerId;
        
        public CartCleared() {
            super();
        }
        
        public CartCleared(int cartId, int customerId) {
            super("cart-" + cartId, "Cart");
            this.cartId = cartId;
            this.customerId = customerId;
        }
        
        public int getCartId() {
            return cartId;
        }
        
        public void setCartId(int cartId) {
            this.cartId = cartId;
        }
        
        public int getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }
    }
} 