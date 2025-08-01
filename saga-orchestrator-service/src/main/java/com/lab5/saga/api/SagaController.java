package com.lab5.saga.api;

import com.lab5.saga.domain.OrderRequest;
import com.lab5.saga.domain.SagaResult;
import com.lab5.saga.service.SagaOrchestratorService;
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
} 