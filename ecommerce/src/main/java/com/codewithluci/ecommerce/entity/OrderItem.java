package com.codewithluci.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    // MANY OrderItems belong to ONE Order
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // MANY OrderItems reference ONE Product
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ─── SNAPSHOT DATA (copied from Product at time of purchase) ───────────

    @NotNull
    @Column(nullable = false, length = 200)
    private String productName;  // Product name at time of order

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;  // Product price at time of order

    @NotNull
    @Min(value = 1)
    @Column(nullable = false)
    private Integer quantity;

    // Calculated field: price * quantity
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Business logic: Calculate subtotal
    public void calculateSubtotal() {
        this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }

    // Helper method: Create from product
    public static OrderItem fromProduct(Product product, int quantity) {
        OrderItem item = OrderItem.builder()
                .product(product)
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
        item.calculateSubtotal();
        return item;
    }

}