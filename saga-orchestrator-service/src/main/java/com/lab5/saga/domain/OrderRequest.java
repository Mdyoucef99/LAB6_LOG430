package com.lab5.saga.domain;

import java.util.List;

public class OrderRequest {
    private int customerId;
    private int cartId;
    private List<OrderItem> items;
    private double totalAmount;

    public OrderRequest() {}

    public OrderRequest(int customerId, int cartId, List<OrderItem> items, double totalAmount) {
        this.customerId = customerId;
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
} 