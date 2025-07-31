package com.lab5.cart.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cart_items")
public class CartItem {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "cart_id", canBeNull = false)
    private int cartId;
    @DatabaseField(columnName = "product_id", canBeNull = false)
    private int productId;
    @DatabaseField(canBeNull = false)
    private int quantity;

    public CartItem() {}
    public CartItem(int cartId, int productId, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }
    public int getId() { return id; }
    public int getCartId() { return cartId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 