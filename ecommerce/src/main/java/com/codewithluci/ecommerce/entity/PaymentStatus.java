package com.codewithluci.ecommerce.entity;


public enum PaymentStatus {
    PENDING,      // Payment initiated
    PROCESSING,   // Payment being processed by gateway
    SUCCESS,      // Payment completed successfully
    FAILED,       // Payment failed
    REFUNDED      // Payment was refunded
}
