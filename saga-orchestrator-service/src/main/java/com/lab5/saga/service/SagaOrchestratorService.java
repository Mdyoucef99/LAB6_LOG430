package com.lab5.saga.service;

import com.lab5.saga.domain.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class SagaOrchestratorService {
    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestratorService.class);
    
    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;
    
    // Prometheus Metrics
    private final Counter sagaStartedCounter;
    private final Counter sagaCompletedCounter;
    private final Counter sagaFailedCounter;
    private final Timer sagaDurationTimer;
    private final Counter compensationTriggeredCounter;
    
    // Service URLs (configured for Docker networking)
    private static final String CART_SERVICE_URL = "http://cart-service:8080";
    private static final String INVENTORY_SERVICE_URL = "http://inventory-service:8080";
    private static final String ORDER_SERVICE_URL = "http://order-service:8080";
    
    @Autowired
    public SagaOrchestratorService(RestTemplate restTemplate, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        
        // Initialize Prometheus metrics
        this.sagaStartedCounter = Counter.builder("saga_started_total")
            .description("Total number of sagas started")
            .register(meterRegistry);
            
        this.sagaCompletedCounter = Counter.builder("saga_completed_total")
            .description("Total number of sagas completed successfully")
            .register(meterRegistry);
            
        this.sagaFailedCounter = Counter.builder("saga_failed_total")
            .description("Total number of sagas that failed")
            .register(meterRegistry);
            
        this.sagaDurationTimer = Timer.builder("saga_duration_seconds")
            .description("Duration of saga execution")
            .register(meterRegistry);
            
        this.compensationTriggeredCounter = Counter.builder("saga_compensation_triggered_total")
            .description("Total number of compensation actions triggered")
            .register(meterRegistry);
    }
    
    public SagaResult processOrderSaga(OrderRequest request) {
        // Generate unique saga ID for tracing
        String sagaId = UUID.randomUUID().toString();
        MDC.put("sagaId", sagaId);
        
        // Start metrics
        sagaStartedCounter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);
        
        logger.info("Starting order saga for customer: {}, cart: {}, sagaId: {}", 
                   request.getCustomerId(), request.getCartId(), sagaId);
        
        try {
            // Step 1: Validate cart
            logger.info("Step 1: Validating cart {} - sagaId: {}", request.getCartId(), sagaId);
            validateCart(request.getCartId());
            logger.info("Cart validation successful - sagaId: {}", sagaId);
            
            // Step 2: Reserve stock
            logger.info("Step 2: Reserving stock for {} items - sagaId: {}", request.getItems().size(), sagaId);
            reserveStock(request.getItems());
            logger.info("Stock reservation successful - sagaId: {}", sagaId);
            
            // Step 3: Create order
            logger.info("Step 3: Creating order - sagaId: {}", sagaId);
            Object order = createOrder(request);
            logger.info("Order created successfully - sagaId: {}", sagaId);
            
            // Step 4: Clear cart
            logger.info("Step 4: Clearing cart {} - sagaId: {}", request.getCartId(), sagaId);
            clearCart(request.getCartId());
            logger.info("Cart cleared successfully - sagaId: {}", sagaId);
            
            // Success metrics
            long duration = sample.stop(sagaDurationTimer);
            sagaCompletedCounter.increment();
            
            logger.info("Saga completed successfully - sagaId: {}, duration: {}ms", 
                       sagaId, duration);
            return SagaResult.success(order);
            
        } catch (Exception e) {
            // Failure metrics
            sample.stop(sagaDurationTimer);
            sagaFailedCounter.increment();
            
            logger.error("Saga failed - sagaId: {}, error: {}", sagaId, e.getMessage(), e);
            compensateOnFailure(request);
            return SagaResult.failed("Order processing failed: " + e.getMessage());
        } finally {
            MDC.remove("sagaId");
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
        compensationTriggeredCounter.increment();
        String sagaId = MDC.get("sagaId");
        logger.info("Starting compensation for failed order - sagaId: {}", sagaId);
        
        try {
            // Release any reserved stock
            String releaseUrl = INVENTORY_SERVICE_URL + "/api/v1/stores/1/stock/release";
            restTemplate.postForEntity(releaseUrl, request.getItems(), String.class);
            logger.info("Stock released successfully during compensation - sagaId: {}", sagaId);
            
        } catch (Exception e) {
            logger.error("Compensation failed - sagaId: {}, error: {}", sagaId, e.getMessage(), e);
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