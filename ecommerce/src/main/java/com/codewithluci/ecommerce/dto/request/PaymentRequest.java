package com.codewithluci.ecommerce.dto.request;

import com.codewithluci.ecommerce.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // In real app, this would contain card details, UPI ID, etc.
    // For security, NEVER store raw card details in your database
    // Always use payment gateway tokenization
    private String paymentToken;  // Token from payment gateway (e.g., Stripe token)
}