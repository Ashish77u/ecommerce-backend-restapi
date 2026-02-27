package com.codewithluci.ecommerce.service;

import com.codewithluci.ecommerce.dto.request.PaymentRequest;
import com.codewithluci.ecommerce.dto.respone.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, Long userId);
    PaymentResponse getPaymentByOrderId(Long orderId, Long userId);
    PaymentResponse simulatePaymentWebhook(String transactionId, boolean success);
}