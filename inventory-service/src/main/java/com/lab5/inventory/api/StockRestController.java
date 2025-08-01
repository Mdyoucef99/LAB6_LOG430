package com.lab5.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.lab5.inventory.domain.Stock;
import com.lab5.inventory.domain.Store;
import com.lab5.inventory.domain.Produit;
import com.lab5.inventory.infrastructure.StockDao;
import com.lab5.inventory.infrastructure.StoreDao;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Stocks", description = "API pour la gestion des stocks par magasin")
public class StockRestController {
    private final StockDao stockDao;
    private final StoreDao storeDao;
    private static final Logger logger = LoggerFactory.getLogger(StockRestController.class);

    @Autowired
    public StockRestController(StockDao stockDao, StoreDao storeDao) {
        this.stockDao = stockDao;
        this.storeDao = storeDao;
    }

    @GetMapping("/{id}/stock")
    @Operation(summary = "Récupérer le stock d'un magasin", description = "Retourne la liste des stocks pour un magasin spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock récupéré avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Stock.class))),
        @ApiResponse(responseCode = "404", description = "Magasin non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Stock>> getStockByStore(@Parameter(description = "ID du magasin") @PathVariable("id") int id) {
        logger.info("Received request to get stock for store with id: {}", id);
        try {
            Store store = storeDao.findById(id);
            if (store == null) {
                logger.info("Store with id {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Stock> stocks = stockDao.listByStore(store);
            logger.info("Stock retrieved successfully for store with id: {}", id);
            return ResponseEntity.ok(stocks);
        } catch (SQLException e) {
            logger.error("Error retrieving stock for store with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{storeId}/stock/reserve")
    @Operation(summary = "Réserver du stock", description = "Réserve du stock pour une commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock réservé avec succès"),
        @ApiResponse(responseCode = "400", description = "Stock insuffisant"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> reserveStock(@Parameter(description = "ID du magasin") @PathVariable("storeId") int storeId,
                                             @RequestBody List<StockReservationRequest> items) {
        logger.info("Reserving stock for store: {}, items: {}", storeId, items.size());
        try {
            Store store = storeDao.findById(storeId);
            if (store == null) {
                logger.warn("Store {} not found", storeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found");
            }

            for (StockReservationRequest item : items) {
                // Create a temporary Produit object for lookup
                Produit produit = new Produit();
                produit.setId(item.getProductId());
                
                Stock stock = stockDao.getStock(store, produit);
                if (stock == null) {
                    logger.warn("Stock not found for product: {} in store: {}", item.getProductId(), storeId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock not found for product: " + item.getProductId());
                }
                
                if (stock.getQuantity() < item.getQuantity()) {
                    logger.warn("Insufficient stock for product: {} in store: {}", item.getProductId(), storeId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock for product: " + item.getProductId());
                }
                
                // Simulate failure for testing (remove in production)
                if (item.getQuantity() > 10) {
                    throw new RuntimeException("Stock reservation failed - too many items");
                }
                
                // Reserve stock by reducing quantity
                stock.setQuantity(stock.getQuantity() - item.getQuantity());
                stockDao.getDao().update(stock);
            }
            
            logger.info("Stock reserved successfully for store: {}", storeId);
            return ResponseEntity.ok("RESERVED");
        } catch (SQLException e) {
            logger.error("Error reserving stock for store: {}", storeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reservation failed");
        }
    }

    @PostMapping("/{storeId}/stock/release")
    @Operation(summary = "Libérer du stock", description = "Libère du stock réservé")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock libéré avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> releaseStock(@Parameter(description = "ID du magasin") @PathVariable("storeId") int storeId,
                                             @RequestBody List<StockReservationRequest> items) {
        logger.info("Releasing stock for store: {}, items: {}", storeId, items.size());
        try {
            Store store = storeDao.findById(storeId);
            if (store == null) {
                logger.warn("Store {} not found", storeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found");
            }

            for (StockReservationRequest item : items) {
                // Create a temporary Produit object for lookup
                Produit produit = new Produit();
                produit.setId(item.getProductId());
                
                Stock stock = stockDao.getStock(store, produit);
                if (stock != null) {
                    // Release stock by increasing quantity
                    stock.setQuantity(stock.getQuantity() + item.getQuantity());
                    stockDao.getDao().update(stock);
                }
            }
            
            logger.info("Stock released successfully for store: {}", storeId);
            return ResponseEntity.ok("RELEASED");
        } catch (SQLException e) {
            logger.error("Error releasing stock for store: {}", storeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Release failed");
        }
    }

    // Inner class for stock reservation requests
    public static class StockReservationRequest {
        private int productId;
        private int quantity;

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
} 