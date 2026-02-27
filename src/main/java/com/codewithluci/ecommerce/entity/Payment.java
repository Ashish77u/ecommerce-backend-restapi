package com.codewithluci.ecommerce.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    // ONE Payment belongs to ONE Order (bi-directional)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    // Payment gateway transaction ID
    @Column(unique = true, length = 100)
    private String transactionId;

    // Payment gateway reference (e.g., Stripe charge ID, PayPal transaction ID)
    @Column(length = 100)
    private String gatewayReference;

    @Column(length = 500)
    private String failureReason;

    // Business methods
    public void markAsSuccess(String transactionId, String gatewayReference) {
        this.status = PaymentStatus.SUCCESS;
        this.transactionId = transactionId;
        this.gatewayReference = gatewayReference;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.SUCCESS;
    }

    public boolean canBeRefunded() {
        return this.status == PaymentStatus.SUCCESS;
    }
}

// update order entity
/*
// ONE Order has ONE Payment
@OneToOne(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY
)
private Payment payment;
 */