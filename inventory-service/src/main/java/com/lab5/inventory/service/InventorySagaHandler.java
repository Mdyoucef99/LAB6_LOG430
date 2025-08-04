package com.lab5.inventory.service;

import com.lab5.inventory.infrastructure.StockDao;
import com.lab5.inventory.domain.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class InventorySagaHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(InventorySagaHandler.class);
    
    private final StockDao stockDao;
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    public InventorySagaHandler(StockDao stockDao, RabbitTemplate rabbitTemplate) {
        this.stockDao = stockDao;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    // Listen for "cart validated" event
    @RabbitListener(queues = "inventory.queue")
    public void handleCartValidated(CartValidatedEvent event) {
        logger.info("Inventory service received cart validated event - cartId: {}", event.getCartId());
        
        // Reserve stock (simplified - assume we have enough stock)
        boolean stockReserved = reserveStock(event.getCartId());
        
        if (stockReserved) {
            // Stock reserved successfully - publish "stock reserved" event
            StockReservedEvent reservedEvent = new StockReservedEvent(event.getCartId());
            rabbitTemplate.convertAndSend("saga.events", "stock.reserved", reservedEvent);
            logger.info("Stock reserved - cartId: {}", event.getCartId());
        } else {
            // Stock reservation failed - publish "stock reservation failed" event
            StockReservationFailedEvent failedEvent = new StockReservationFailedEvent(event.getCartId(), "Insufficient stock");
            rabbitTemplate.convertAndSend("saga.events", "stock.reservation.failed", failedEvent);
            logger.error("Stock reservation failed - cartId: {}", event.getCartId());
        }
    }
    
    // Simple stock reservation logic
    private boolean reserveStock(int cartId) {
        // Simplified - assume we have enough stock for cartId < 10
        return cartId < 10;
    }
    
    // Simple event classes
    public static class CartValidatedEvent {
        private int cartId;
        public CartValidatedEvent() {}
        public CartValidatedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class StockReservedEvent {
        private int cartId;
        public StockReservedEvent() {}
        public StockReservedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class StockReservationFailedEvent {
        private int cartId;
        private String reason;
        public StockReservationFailedEvent() {}
        public StockReservationFailedEvent(int cartId, String reason) {
            this.cartId = cartId;
            this.reason = reason;
        }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
} 