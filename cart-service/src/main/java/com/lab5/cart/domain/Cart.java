package com.lab5.cart.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "carts")
public class Cart {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "customer_id", canBeNull = false)
    private int customerId;

    public Cart() {}
    public Cart(int customerId) { this.customerId = customerId; }
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
} 