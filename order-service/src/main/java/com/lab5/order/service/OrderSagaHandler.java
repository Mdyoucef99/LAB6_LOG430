package com.lab5.order.service;

import com.lab5.order.infrastructure.OrderDao;
import com.lab5.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class OrderSagaHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderSagaHandler.class);
    
    private final OrderDao orderDao;
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    public OrderSagaHandler(OrderDao orderDao, RabbitTemplate rabbitTemplate) {
        this.orderDao = orderDao;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    // Listen for "stock reserved" event
    @RabbitListener(queues = "order.queue")
    public void handleStockReserved(StockReservedEvent event) {
        logger.info("Order service received stock reserved event - cartId: {}", event.getCartId());
        
        try {
            // Create the order using cartId as customerId and a default total amount
            // In a real scenario, we would calculate the total from cart items
            Order order = new Order(event.getCartId(), 100.0); // Using cartId as customerId, default amount
            orderDao.create(order);
            
            // Publish "order created" event
            OrderCreatedEvent createdEvent = new OrderCreatedEvent(event.getCartId());
            rabbitTemplate.convertAndSend("saga.events", "order.created", createdEvent);
            logger.info("Order created - cartId: {}, orderId: {}", event.getCartId(), order.getId());
            
        } catch (SQLException e) {
            logger.error("Failed to create order: {}", e.getMessage());
        }
    }
    
    // Simple event classes
    public static class StockReservedEvent {
        private int cartId;
        public StockReservedEvent() {}
        public StockReservedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    public static class OrderCreatedEvent {
        private int cartId;
        public OrderCreatedEvent() {}
        public OrderCreatedEvent(int cartId) { this.cartId = cartId; }
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
} 