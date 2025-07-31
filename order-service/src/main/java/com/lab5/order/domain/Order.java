package com.lab5.order.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "orders")
public class Order {
    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField(columnName = "customer_id", canBeNull = false)
    private int customerId;
    
    @DatabaseField(columnName = "total_amount", canBeNull = false)
    private double totalAmount;
    
    @DatabaseField(columnName = "order_date", canBeNull = false)
    private Date orderDate;

    public Order() {}

    public Order(int customerId, double totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderDate = new Date();
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getOrderDate() { return orderDate; }
    
    public void setId(int id) { this.id = id; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
} 