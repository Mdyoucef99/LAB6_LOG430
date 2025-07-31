package com.lab5.inventory.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stocks")
public class Stock {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true,  columnName = "store_id", canBeNull = false, uniqueCombo = true)
    private Store store;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "product_id", canBeNull = false, uniqueCombo = true)
    private Produit produit;

    @DatabaseField(canBeNull = false)
    private int quantity;

    public Stock() { }

    public Stock(Store store, Produit produit, int quantity) {
        this.store = store;
        this.produit = produit;
        this.quantity = quantity;
    }

    public Store getStore() { return store; }
    public Produit getProduit() { return produit; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 