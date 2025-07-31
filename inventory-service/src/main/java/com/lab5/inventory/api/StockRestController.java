package com.lab5.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.lab5.inventory.domain.Stock;
import com.lab5.inventory.domain.Store;
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
} 