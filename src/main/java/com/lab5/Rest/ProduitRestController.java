package com.lab5.Rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Model.Produit;
import com.lab5.dao.ProduitDao;

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


import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Produits", description = "API pour la gestion des produits")
public class ProduitRestController {
    private final ProduitDao produitDao;

    public ProduitRestController() {
        try {
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String databaseUrl = "jdbc:postgresql://" + host + ":" + port + "/magasin";
            String user = "magasin_user";
            String password = "magasinpswd";
            ConnectionSource cs = new JdbcConnectionSource(databaseUrl, user, password);
            this.produitDao = new ProduitDao(cs);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'initialisation de la connexion à la base", e);
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les produits", description = "Retourne la liste complète de tous les produits disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Produit.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Produit>> getAll() {
        try {
            return ResponseEntity.ok(produitDao.getInventaire());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit par ID", description = "Retourne les détails d'un produit spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit trouvé",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Produit.class))),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Produit> getById(@Parameter(description = "ID du produit à récupérer") @PathVariable("id") int id) {
        try {
            Produit p = produitDao.rechercherParId(id);
            if (p == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(p);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau produit", description = "Ajoute un nouveau produit à l'inventaire")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produit créé avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Produit.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Produit> create(@Parameter(description = "Détails du produit à créer") @RequestBody Produit produit) {
        try {
            produitDao.ajouterProduit(produit);
            return ResponseEntity.status(HttpStatus.CREATED).body(produit);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit", description = "Modifie les informations d'un produit existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Produit.class))),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Produit> update(@Parameter(description = "ID du produit à mettre à jour") @PathVariable("id") int id, 
                                        @Parameter(description = "Nouvelles informations du produit") @RequestBody Produit produit) {
        try {
            Produit existing = produitDao.rechercherParId(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            // Met à jour les champs
            existing.setNom(produit.getNom());
            existing.setCategorie(produit.getCategorie());
            existing.setPrix(produit.getPrix());
            existing.setQuantite(produit.getQuantite());
            produitDao.ajouterProduit(existing); // createIfNotExists fait update si existe
            return ResponseEntity.ok(existing);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit", description = "Supprime un produit de l'inventaire")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID du produit à supprimer") @PathVariable("id") int id) {
        try {
            Produit existing = produitDao.rechercherParId(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            // Suppression via DAO (à ajouter si besoin)
            produitDao.getDao().deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 