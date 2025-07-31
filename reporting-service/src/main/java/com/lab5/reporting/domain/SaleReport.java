package com.lab5.reporting.domain;

public class SaleReport {
    private final String storeName;
    private final String productName;
    private final long totalQty;

    public SaleReport(String storeName, String productName, long totalQty) {
        this.storeName   = storeName;
        this.productName = productName;
        this.totalQty    = totalQty;
    }

    public String getStoreName()   { return storeName; }
    public String getProductName() { return productName; }
    public long   getTotalQty()    { return totalQty; }
} 