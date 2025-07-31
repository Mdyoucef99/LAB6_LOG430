package com.lab5.Rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.SQLException;
import java.util.Map;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Controller.DashboardController;
import com.lab5.dao.ProduitDao;
import com.lab5.dao.SaleDao;
import com.lab5.dao.StockDao;
import com.lab5.dao.StoreDao;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "API pour les données du tableau de bord")
public class DashboardRestController {
    private final DashboardController dashboardController;

    public DashboardRestController() {
        try {
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String databaseUrl = "jdbc:postgresql://" + host + ":" + port + "/magasin";
            String user = "magasin_user";
            String password = "magasinpswd";
            ConnectionSource cs = new JdbcConnectionSource(databaseUrl, user, password);
            StoreDao storeDao = new StoreDao(cs);
            ProduitDao produitDao = new ProduitDao(cs);
            StockDao stockDao = new StockDao(cs);
            SaleDao saleDao = new SaleDao(cs);
            this.dashboardController = new DashboardController(storeDao, produitDao, stockDao, saleDao);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'initialisation de la connexion à la base", e);
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer les données du dashboard", description = "Retourne les statistiques consolidées du tableau de bord")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Données du dashboard récupérées avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(description = "Map contenant les données du dashboard"))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Map<String, Object>> getDashboard() {
        try {
            Map<String, Object> dashboard = dashboardController.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
} 