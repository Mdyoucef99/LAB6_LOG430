package com.lab5.saga.service;

import com.lab5.saga.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SagaOrchestratorService {
    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestratorService.class);
    
    private final RestTemplate restTemplate;
    
    // Service URLs (configured for Docker networking)
    private static final String CART_SERVICE_URL = "http://cart-service:8080";
    private static final String INVENTORY_SERVICE_URL = "http://inventory-service:8080";
    private static final String ORDER_SERVICE_URL = "http://order-service:8080";
    
    @Autowired
    public SagaOrchestratorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public SagaResult processOrderSaga(OrderRequest request) {
        logger.info("Starting order saga for customer: {}, cart: {}", 
                   request.getCustomerId(), request.getCartId());
        
        try {
            // Step 1: Validate cart
            logger.info("Step 1: Validating cart {}", request.getCartId());
            validateCart(request.getCartId());
            logger.info("Cart validation successful");
            
            // Step 2: Reserve stock
            logger.info("Step 2: Reserving stock for {} items", request.getItems().size());
            reserveStock(request.getItems());
            logger.info("Stock reservation successful");
            
            // Step 3: Create order
            logger.info("Step 3: Creating order");
            Object order = createOrder(request);
            logger.info("Order created successfully");
            
            // Step 4: Clear cart
            logger.info("Step 4: Clearing cart {}", request.getCartId());
            clearCart(request.getCartId());
            logger.info("Cart cleared successfully");
            
            logger.info("Saga completed successfully");
            return SagaResult.success(order);
            
        } catch (Exception e) {
            logger.error("Saga failed: {}", e.getMessage(), e);
            compensateOnFailure(request);
            return SagaResult.failed("Order processing failed: " + e.getMessage());
        }
    }
    
    private void validateCart(int cartId) {
        String url = CART_SERVICE_URL + "/api/v1/carts/" + cartId + "/validate";
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Cart validation failed");
        }
    }
    
    private void reserveStock(List<OrderItem> items) {
        String url = INVENTORY_SERVICE_URL + "/api/v1/stores/1/stock/reserve";
        ResponseEntity<String> response = restTemplate.postForEntity(url, items, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Stock reservation failed");
        }
    }
    
    private Object createOrder(OrderRequest request) {
        String url = ORDER_SERVICE_URL + "/api/v1/orders/checkout";
        
        // Create checkout request for order service
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCustomerId(request.getCustomerId());
        checkoutRequest.setTotalAmount(request.getTotalAmount());
        
        ResponseEntity<Object> response = restTemplate.postForEntity(url, checkoutRequest, Object.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Order creation failed");
        }
        
        return response.getBody();
    }
    
    private void clearCart(int cartId) {
        String url = CART_SERVICE_URL + "/api/v1/carts/" + cartId + "/clear";
        restTemplate.delete(url);
    }
    
    private void compensateOnFailure(OrderRequest request) {
        logger.info("Starting compensation for failed order");
        
        try {
            // Release any reserved stock
            String releaseUrl = INVENTORY_SERVICE_URL + "/api/v1/stores/1/stock/release";
            restTemplate.postForEntity(releaseUrl, request.getItems(), String.class);
            logger.info("Stock released successfully during compensation");
            
        } catch (Exception e) {
            logger.error("Compensation failed: {}", e.getMessage(), e);
        }
    }
    
    // Inner class for checkout request
    public static class CheckoutRequest {
        private int customerId;
        private double totalAmount;

        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }

        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    }
} 