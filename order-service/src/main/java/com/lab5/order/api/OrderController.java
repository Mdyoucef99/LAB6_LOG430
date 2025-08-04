package com.lab5.order.api;

import com.lab5.order.domain.Order;
import com.lab5.order.infrastructure.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Commandes", description = "API pour la gestion des commandes")
public class OrderController {
    private final OrderDao orderDao;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(OrderDao orderDao, RabbitTemplate rabbitTemplate) {
        this.orderDao = orderDao;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les commandes", description = "Retourne la liste complète de toutes les commandes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            logger.info("Attempting to retrieve all orders");
            List<Order> orders = orderDao.findAll();
            logger.info("Successfully retrieved {} orders", orders != null ? orders.size() : 0);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par ID", description = "Retourne les détails d'une commande spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Order> getOrderById(@Parameter(description = "ID de la commande") @PathVariable("id") int id) {
        try {
            logger.info("Attempting to retrieve order with ID: {}", id);
            Order order = orderDao.findById(id);
            if (order == null) {
                logger.info("Order with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Successfully retrieved order with ID: {}", id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving order {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/checkout")
    @Operation(summary = "Valider une commande (checkout)", description = "Crée une nouvelle commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commande créée avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Données de commande invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Order> checkout(@Parameter(description = "Détails de la commande") @RequestBody CheckoutRequest request) {
        try {
            logger.info("Attempting to create order for customer: {}, total: {}", 
                       request.getCustomerId(), request.getTotalAmount());
            
            // Créer la commande
            Order order = new Order(request.getCustomerId(), request.getTotalAmount());
            orderDao.create(order);
            
            logger.info("Successfully created order");
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            logger.error("Error occurred while creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // Simple Choreographed Saga endpoint
    @PostMapping("/start-saga")
    public ResponseEntity<String> startChoreographedSaga(@RequestBody SagaRequest request) {
        logger.info("Starting choreographed saga - cartId: {}", request.getCartId());
        
        // Publish "order started" event to trigger the saga
        OrderStartedEvent event = new OrderStartedEvent(request.getCartId());
        rabbitTemplate.convertAndSend("saga.events", "order.started", event);
        
        return ResponseEntity.ok("Choreographed saga started for cart: " + request.getCartId());
    }

    // Classe de requête pour l'endpoint checkout
    public static class CheckoutRequest {
        private int customerId;
        private double totalAmount;

        public int getCustomerId() { return customerId; }
        public double getTotalAmount() { return totalAmount; }
        
        public void setCustomerId(int customerId) { this.customerId = customerId; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    }
    
    // Simple request for saga
    public static class SagaRequest {
        private int cartId;
        
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
    
    // Simple event class
    public static class OrderStartedEvent {
        private int cartId;
        
        public OrderStartedEvent() {}
        public OrderStartedEvent(int cartId) { this.cartId = cartId; }
        
        public int getCartId() { return cartId; }
        public void setCartId(int cartId) { this.cartId = cartId; }
    }
} 