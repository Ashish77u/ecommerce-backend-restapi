package com.codewithluci.ecommerce.entity;

public enum OrderStatus {
    PENDING,        // Order created, awaiting payment
    CONFIRMED,      // Payment successful
    PROCESSING,     // Order being prepared
    SHIPPED,        // Order dispatched
    DELIVERED,      // Order completed
    CANCELLED       // Order cancelled (payment failed or user cancelled)
}


/*

    // ==================================== enum =======================
    ---> In Java, an enum (short for enumeration) is a special data type used to define a fixed set of constants,
    which provides type safety and makes the code more readable and maintainable


 */