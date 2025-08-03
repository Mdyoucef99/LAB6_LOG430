package com.lab5.cart.domain.events;

public class ArticleAjoute extends CartEvent {
    private int cartId;
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    private int customerId;

    public ArticleAjoute() {
        super();
    }

    public ArticleAjoute(int cartId, int productId, String productName, int quantity, double price, int customerId) {
        super("cart-" + cartId, "Cart");
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.customerId = customerId;
    }

    // Getters and Setters
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
} 