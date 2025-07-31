package com.lab5.reporting.api;

import org.springframework.web.bind.annotation.*;
import com.lab5.reporting.domain.SaleReport;
import com.lab5.reporting.infrastructure.ProduitDao;
import com.lab5.reporting.infrastructure.SaleDao;
import com.lab5.reporting.infrastructure.StoreDao;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Rapports", description = "API pour la génération de rapports")
public class ReportRestController {
    private final SaleDao saleDao;
    private final StoreDao storeDao;
    private final ProduitDao produitDao;

    @Autowired
    public ReportRestController(SaleDao saleDao, StoreDao storeDao, ProduitDao produitDao) {
        this.saleDao = saleDao;
        this.storeDao = storeDao;
        this.produitDao = produitDao;
    }

    @GetMapping("/sales")
    @Operation(summary = "Rapport de ventes consolidé", description = "Génère un rapport consolidé des ventes par magasin et produit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rapport généré avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SaleReport.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<SaleReport>> getConsolidatedSalesReport() {
        try {
            List<SaleReport> report = saleDao.consolidatedReport(storeDao, produitDao);
            return ResponseEntity.ok(report);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
} 