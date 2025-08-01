package com.lab5.saga.domain;

public enum SagaState {
    INITIATED,
    CART_VALIDATED,
    STOCK_RESERVED,
    ORDER_CREATED,
    CART_CLEARED,
    COMPLETED,
    FAILED,
    COMPENSATION_IN_PROGRESS
} 