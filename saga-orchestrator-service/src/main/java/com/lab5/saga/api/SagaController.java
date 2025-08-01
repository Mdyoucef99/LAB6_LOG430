package com.lab5.saga.api;

import com.lab5.saga.domain.OrderRequest;
import com.lab5.saga.domain.OrderItem;
import com.lab5.saga.domain.SagaResult;
import com.lab5.saga.service.SagaOrchestratorService;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sagas")
public class SagaController {
    private static final Logger logger = LoggerFactory.getLogger(SagaController.class);
    
    private final SagaOrchestratorService sagaOrchestratorService;
    
    @Autowired
    public SagaController(SagaOrchestratorService sagaOrchestratorService) {
        this.sagaOrchestratorService = sagaOrchestratorService;
    }
    
    @PostMapping("/orders")
    public ResponseEntity<SagaResult> processOrderSaga(@RequestBody OrderRequest request) {
        logger.info("Received order saga request for customer: {}, cart: {}", 
                   request.getCustomerId(), request.getCartId());
        
        try {
            SagaResult result = sagaOrchestratorService.processOrderSaga(request);
            
            if (result.isSuccess()) {
                logger.info("Order saga completed successfully");
                return ResponseEntity.ok(result);
            } else {
                logger.error("Order saga failed: {}", result.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error in order saga: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SagaResult.failed("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Saga Orchestrator Service is running");
    }
    
    @GetMapping("/status")
    public ResponseEntity<Object> getStatus() {
        return ResponseEntity.ok(Map.of(
            "service", "Saga Orchestrator",
            "status", "RUNNING",
            "version", "1.0.0",
            "endpoints", List.of(
                "POST /api/v1/sagas/orders - Process order saga",
                "GET /api/v1/sagas/health - Health check",
                "GET /api/v1/sagas/status - Service status"
            ),
            "timestamp", new java.util.Date()
        ));
    }
    
    @GetMapping("/test")
    public ResponseEntity<Object> testSaga() {
        // Create a test order request
        OrderRequest testRequest = new OrderRequest();
        testRequest.setCustomerId(1);
        testRequest.setCartId(1);
        testRequest.setItems(List.of(new OrderItem(1, 2)));
        testRequest.setTotalAmount(5.0);
        
        return ResponseEntity.ok(Map.of(
            "message", "Test saga request created",
            "testRequest", testRequest,
            "note", "Use POST /api/v1/sagas/orders to actually process this request"
        ));
    }
} 