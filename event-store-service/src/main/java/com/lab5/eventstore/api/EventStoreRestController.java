package com.lab5.eventstore.api;

import com.lab5.eventstore.domain.EventStore;
import com.lab5.eventstore.infrastructure.EventStoreDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/eventstore")
public class EventStoreRestController {
    
    @Autowired
    private EventStoreDao eventStoreDao;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Event Store Service is healthy");
    }
    
    // Stocker un événement
    @PostMapping("/events")
    public ResponseEntity<String> storeEvent(@RequestBody Map<String, Object> eventData) {
        try {
            String eventId = (String) eventData.get("eventId");
            String eventType = (String) eventData.get("eventType");
            String aggregateId = (String) eventData.get("aggregateId");
            Integer version = eventData.get("version") != null ? (Integer) eventData.get("version") : 1;
            
            String eventJson = objectMapper.writeValueAsString(eventData);
            
            EventStore event = new EventStore(eventId, eventType, aggregateId, 
                                            eventJson, LocalDateTime.now(), version);
            
            eventStoreDao.create(event);
            return ResponseEntity.ok("Event stored successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error storing event: " + e.getMessage());
        }
    }
    
    // Récupérer tous les événements
    @GetMapping("/events")
    public ResponseEntity<List<EventStore>> getAllEvents() {
        try {
            return ResponseEntity.ok(eventStoreDao.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Récupérer les événements d'un agrégat
    @GetMapping("/events/aggregate/{aggregateId}")
    public ResponseEntity<List<EventStore>> getEventsForAggregate(@PathVariable String aggregateId) {
        try {
            List<EventStore> events = eventStoreDao.findByAggregateId(aggregateId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Replay depuis une version
    @GetMapping("/events/replay/{aggregateId}")
    public ResponseEntity<List<EventStore>> replayFromVersion(
            @PathVariable String aggregateId, 
            @RequestParam(defaultValue = "1") Integer fromVersion) {
        try {
            List<EventStore> events = eventStoreDao.findByAggregateIdFromVersion(aggregateId, fromVersion);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Reconstituer l'état courant d'un panier
    @GetMapping("/projections/cart/{cartId}")
    public ResponseEntity<Map<String, Object>> getCurrentCartState(@PathVariable String cartId) {
        try {
            List<EventStore> events = eventStoreDao.findByAggregateId("cart-" + cartId);
            
            if (events.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Reconstituer l'état depuis les événements
            Map<String, Object> cartState = new HashMap<>();
            cartState.put("cartId", cartId);
            cartState.put("items", new HashMap<Integer, Object>());
            cartState.put("customerId", null);
            cartState.put("version", 0);
            
            @SuppressWarnings("unchecked")
            Map<Integer, Object> items = (Map<Integer, Object>) cartState.get("items");
            
            for (EventStore event : events) {
                Map<String, Object> eventData = objectMapper.readValue(event.getEventData(), Map.class);
                
                switch (event.getEventType()) {
                    case "ArticleAjoute":
                        Integer productId = (Integer) eventData.get("productId");
                        Integer quantity = (Integer) eventData.get("quantity");
                        String productName = (String) eventData.get("productName");
                        Double price = (Double) eventData.get("price");
                        Integer customerId = (Integer) eventData.get("customerId");
                        
                        cartState.put("customerId", customerId);
                        
                        Map<String, Object> item = new HashMap<>();
                        item.put("productId", productId);
                        item.put("productName", productName);
                        item.put("quantity", quantity);
                        item.put("price", price);
                        
                        items.put(productId, item);
                        break;
                        
                    case "CartCleared":
                        items.clear();
                        break;
                }
                cartState.put("version", event.getVersion());
            }
            
            cartState.put("totalItems", items.size());
            cartState.put("lastUpdated", LocalDateTime.now());
            
            return ResponseEntity.ok(cartState);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error reconstructing cart state: " + e.getMessage()));
        }
    }
}