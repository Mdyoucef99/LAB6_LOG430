package com.lab5.reporting.domain;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sales")
public class Sale {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName="store_id")
    private Store store;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName="product_id")
    private Produit produit;

    @DatabaseField(canBeNull = false)
    private int quantity;

    @DatabaseField(columnName = "saleDate", canBeNull = false)
    private Date saleDate;

    public Sale() { }

    public Sale(Store store, Produit produit, int quantity) {
        this.store = store;
        this.produit = produit;
        this.quantity = quantity;
        this.saleDate = new Date();
    }

    public Store getStore() { return store; }
    public Produit getProduit() { return produit; }
    public int getQuantity() { return quantity; }
    public Date getSaleDate() { return saleDate; }
} 