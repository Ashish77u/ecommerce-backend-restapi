package com.codewithluci.ecommerce.controller;

import com.codewithluci.ecommerce.dto.request.PaymentRequest;
import com.codewithluci.ecommerce.dto.respone.ApiResponse;
import com.codewithluci.ecommerce.dto.respone.PaymentResponse;
import com.codewithluci.ecommerce.service.PaymentService;
import com.codewithluci.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    /**
     * Process payment for an order
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        log.info("Payment request for order: {}", request.getOrderId());

        Long userId = getUserIdFromAuthentication(authentication);

        PaymentResponse payment = paymentService.processPayment(request, userId);

        HttpStatus status = payment.getStatus().name().equals("SUCCESS")
                ? HttpStatus.OK
                : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ApiResponse.success("Payment processed", payment),
                status
        );
    }

    /**
     * Get payment details by order ID
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @PathVariable Long orderId,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Payment details retrieved", payment)
        );
    }

    /**
     * Simulate payment gateway webhook (for testing only)
     * In production, this would be called by actual payment gateway
     */
    @PostMapping("/webhook/simulate")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulateWebhook(
            @RequestParam String transactionId,
            @RequestParam boolean success) {

        log.info("Simulating payment webhook: {} - {}", transactionId, success ? "SUCCESS" : "FAILURE");

        PaymentResponse payment = paymentService.simulatePaymentWebhook(transactionId, success);

        return ResponseEntity.ok(
                ApiResponse.success("Webhook processed", payment)
        );
    }

    // Helper method
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username).getId();
    }
}