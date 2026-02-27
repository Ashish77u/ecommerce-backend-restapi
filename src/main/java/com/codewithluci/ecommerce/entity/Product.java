package com.codewithluci.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Use BigDecimal for money - NEVER use float or double
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(length = 500)
    private String imageUrl;

    // SKU = Stock Keeping Unit (unique product identifier)
    @Column(unique = true, length = 100)
    private String sku;

    @Column(nullable = false)
    private Boolean isActive = true;

    // MANY Products belong to ONE Category
    @ManyToOne(
            fetch = FetchType.LAZY,             // Don't load category unless needed
            optional = false                    // Product MUST have a category
    )
    @JoinColumn(
            name = "category_id",              // FK column name in products table
            nullable = false
    )
    private Category category;

    // Business method to reduce stock safely
    public void reduceStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product: " + this.name +
                            ". Available: " + this.stockQuantity +
                            ", Requested: " + quantity
            );
        }
        this.stockQuantity -= quantity;
    }

    // Business method to restore stock (on order cancel/payment fail)
    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // Check if product is in stock
    public boolean isInStock() {
        return this.isActive && this.stockQuantity > 0;
    }

    public boolean hasEnoughStock(int requestedQuantity) {
        return this.stockQuantity >= requestedQuantity;
    }
}
