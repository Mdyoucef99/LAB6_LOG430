package com.lab5.cart.service;

import com.lab5.cart.infrastructure.CartDao;
import com.lab5.cart.infrastructure.CartItemDao;
import com.lab5.cart.domain.Cart;
import com.lab5.cart.domain.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CartSagaHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CartSagaHandler.class);
    
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    public CartSagaHandler(CartDao cartDao, CartItemDao cartItemDao, RabbitTemplate rabbitTemplate) {
        this.cartDao = cartDao;
        this.cartItemDao = cartItemDao;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    // Listen for "order started" event
    @RabbitListener(queues = "cart.queue")
    public void handleOrderStarted(OrderStartedEvent event) {
        logger.info("Cart service received order started event - cartId: {}", event.getCartId());
        
        try {
            // Validate the cart
            Cart cart = cartDao.findById(event.getCartId());
            if (cart != null && !cartItemDao.findByCartId(event.getCartId()).isEmpty()) {
                // Cart is valid - publish "cart validated" event
                CartValidatedEvent validatedEvent = new CartValidatedEvent(event.getCartId());
                rabbitTemplate.convertAndSend("saga.events", "cart.validated", validatedEvent);
                logger.info("Cart validated - cartId: {}", event.getCartId());
            } else {
                // Cart is invalid - publish "cart validation failed" event
                CartValidationFailedEvent failedEvent = new CartValidationFailedEvent(event.getCartId(), "Cart is empty or invalid");
                rabbitTemplate.convertAndSend("saga.events", "cart.validation.failed", failedEvent);
                logger.error("Cart validation failed - cartId: {}", event.getCartId());
            }
        } catch (SQLException e) {
            logger.error("Database error during cart validation: {}", e.getMessage());
        }
    }
    
    // Listen for "order created" event
    @RabbitListener(queues = "cart.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Cart service received order created event - cartId: {}", event.getCartId());
        
        try {
            // Clear the cart
            List<CartItem> items = cartItemDao.findByCartId(event.getCartId());
            for (CartItem item : items) {
                cartItemDao.deleteById(item.getId());
            }
            
            // Publish "cart cleared" event
            CartClearedEvent clearedEvent = new CartClearedEvent(event.getCartId());
            rabbitTemplate.convertAndSend("saga.events", "cart.cleared", clearedEvent);
            logger.info("Cart cleared - cartId: {}", event.getCartId());
            
        } catch (SQLException e) {
            logger.error("Failed to clear cart: {}", e.getMessage());
        }
    }
    
    // Simple event classes
    public static class OrderStartedEvent {
        private int cartId;
        public OrderStartedEvent() {}
        public OrderStartedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class CartValidatedEvent {
        private int cartId;
        public CartValidatedEvent() {}
        public CartValidatedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class CartValidationFailedEvent {
        private int cartId;
        private String reason;
        public CartValidationFailedEvent() {}
        public CartValidationFailedEvent(int cartId, String reason) {
            this.cartId = cartId;
            this.reason = reason;
        }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    public static class OrderCreatedEvent {
        private int cartId;
        public OrderCreatedEvent() {}
        public OrderCreatedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class CartClearedEvent {
        private int cartId;
        public CartClearedEvent() {}
        public CartClearedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
} 