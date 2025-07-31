package com.lab5.inventory.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "produits")
@Schema(description = "Modèle représentant un produit")
public class Produit {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String nom;
    @DatabaseField
    private String categorie;
    @DatabaseField
    private double prix;
    @DatabaseField
    private int quantite;
    public Produit() {}
    public Produit(int id, String nom, String categorie, double prix, int quantite) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.quantite = quantite;
    }
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getCategorie() { return categorie; }
    public double getPrix() { return prix; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setPrix(double prix) { this.prix = prix; }
    @Override
    public String toString() {
        return id + " - " + nom + " (" + categorie + ") : " + prix + " $ [" + quantite + " en stock]";
    }
} 