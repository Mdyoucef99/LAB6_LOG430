package com.lab5.notification.service;

import com.lab5.notification.domain.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void processEvent(BaseEvent event) {
        try {
            logger.info("Processing event: {} - Type: {} - Customer: {}", 
                event.getEventId(), event.getEventType(), event.getCustomerId());
            
            // Process based on event type
            switch (event.getEventType()) {
                case "ArticleAjoute":
                    sendArticleAddedNotification(event);
                    break;
                case "CartCleared":
                    sendCartClearedNotification(event);
                    break;
                case "CartModified":
                    sendCartModifiedNotification(event);
                    break;
                case "CommandeCreee":
                    sendOrderCreatedNotification(event);
                    break;
                case "CommandeConfirmee":
                    sendOrderConfirmedNotification(event);
                    break;
                default:
                    logger.info("Unknown event type: {}", event.getEventType());
            }
            
            logger.info("Successfully processed event: {}", event.getEventId());
            
        } catch (Exception e) {
            logger.error("Error processing event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to process event", e);
        }
    }
    
    private void sendArticleAddedNotification(BaseEvent event) {
        String message = String.format(
            " Article ajouté au panier\n" +
            "Client: %d\n" +
            "Panier: %d\n" +
            "Produit: %s (ID: %d)\n" +
            "Quantité: %d\n" +
            "Prix: %.2f€\n" +
            "Heure: %s",
            event.getCustomerId(),
            event.getCartId(),
            event.getProductName(),
            event.getProductId(),
            event.getQuantity(),
            event.getPrice(),
            event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        // Simulate sending to Slack
        sendSlackNotification(message);
        
        // Simulate sending email
        sendEmailNotification(
            "customer" + event.getCustomerId() + "@example.com",
            "Article ajouté au panier",
            message
        );
    }
    
    private void sendCartClearedNotification(BaseEvent event) {
        String message = String.format(
            " Panier vidé\n" +
            "Client: %d\n" +
            "Panier: %d\n" +
            "Heure: %s",
            event.getCustomerId(),
            event.getCartId(),
            event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        sendSlackNotification(message);
        sendEmailNotification(
            "customer" + event.getCustomerId() + "@example.com",
            "Panier vidé",
            message
        );
    }
    
    private void sendCartModifiedNotification(BaseEvent event) {
        String message = String.format(
            " Panier modifié\n" +
            "Client: %d\n" +
            "Panier: %d\n" +
            "Heure: %s",
            event.getCustomerId(),
            event.getCartId(),
            event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        sendSlackNotification(message);
        sendEmailNotification(
            "customer" + event.getCustomerId() + "@example.com",
            "Panier modifié",
            message
        );
    }
    
    private void sendOrderCreatedNotification(BaseEvent event) {
        String message = String.format(
            " Commande créée\n" +
            "Client: %d\n" +
            "Commande: %d\n" +
            "Montant: %.2f€\n" +
            "Heure: %s",
            event.getCustomerId(),
            event.getOrderId(),
            event.getTotalAmount(),
            event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        sendSlackNotification(message);
        sendEmailNotification(
            "customer" + event.getCustomerId() + "@example.com",
            "Commande créée",
            message
        );
    }
    
    private void sendOrderConfirmedNotification(BaseEvent event) {
        String message = String.format(
            " Commande confirmée\n" +
            "Client: %d\n" +
            "Commande: %d\n" +
            "Montant: %.2f€\n" +
            "Heure: %s",
            event.getCustomerId(),
            event.getOrderId(),
            event.getTotalAmount(),
            event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        sendSlackNotification(message);
        sendEmailNotification(
            "customer" + event.getCustomerId() + "@example.com",
            "Commande confirmée",
            message
        );
    }
    
    private void sendSlackNotification(String message) {
        // Simulate Slack notification
        logger.info("📱 Slack notification sent: {}", message);
    }
    
    private void sendEmailNotification(String to, String subject, String body) {
        // Simulate email notification
        logger.info("📧 Email sent to {} - Subject: {} - Body: {}", to, subject, body);
    }
} 