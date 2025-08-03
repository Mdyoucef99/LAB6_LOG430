package com.lab5.notification.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("Notification service health check");
        return ResponseEntity.ok("Notification Service is running");
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "Notification Service");
        status.put("status", "RUNNING");
        status.put("timestamp", LocalDateTime.now());
        status.put("description", "Service that consumes events and sends notifications");
        status.put("endpoints", new String[]{
            "GET /api/v1/notifications/health - Health check",
            "GET /api/v1/notifications/status - Service status"
        });
        
        logger.info("Notification service status requested");
        return ResponseEntity.ok(status);
    }
} 