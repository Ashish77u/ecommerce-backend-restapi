package com.codewithluci.ecommerce.service.impl;

import com.codewithluci.ecommerce.dto.request.PaymentRequest;
import com.codewithluci.ecommerce.dto.respone.PaymentResponse;
import com.codewithluci.ecommerce.entity.*;
import com.codewithluci.ecommerce.exception.ResourceNotFoundException;
import com.codewithluci.ecommerce.exception.paymentException.PaymentException;
import com.codewithluci.ecommerce.repository.OrderRepository;
import com.codewithluci.ecommerce.repository.PaymentRepository;
import com.codewithluci.ecommerce.repository.ProductRepository;
import com.codewithluci.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        log.info("Processing payment for order: {}", request.getOrderId());

        // 1. Validate order exists and belongs to user
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or access denied"));

        // 2. Validate order is in PENDING state
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new PaymentException(
                    "Cannot process payment for order in status: " + order.getStatus());
        }

        // 3. Check if payment already exists for this order
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new PaymentException("Payment already exists for this order");
        }

        // 4. Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PROCESSING)
                .method(request.getPaymentMethod())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // 5. Simulate payment gateway call
        boolean paymentSuccess = simulatePaymentGateway(request);

        // 6. Handle payment result
        if (paymentSuccess) {
            handlePaymentSuccess(savedPayment);
        } else {
            handlePaymentFailure(savedPayment, order);
        }

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, Long userId) {
        // Validate order belongs to user
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or access denied"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for order: " + orderId));

        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse simulatePaymentWebhook(String transactionId, boolean success) {
        log.info("Processing payment webhook for transaction: {}", transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with transaction ID: " + transactionId));

        if (success) {
            handlePaymentSuccess(payment);
        } else {
            handlePaymentFailure(payment, payment.getOrder());
        }

        return mapToResponse(payment);
    }

    // ─── PRIVATE HELPER METHODS ──────────────────────────────────────────────

    /**
     * Simulate payment gateway API call
     * In real app, this would call Stripe/PayPal/Razorpay API
     */
    private boolean simulatePaymentGateway(PaymentRequest request) {
        log.info("Simulating payment gateway call...");

        // Simulate processing delay
        try {
            Thread.sleep(1000);  // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate success/failure based on payment method
        // In real app, you'd call actual payment gateway API here
        switch (request.getPaymentMethod()) {
            case CREDIT_CARD:
            case DEBIT_CARD:
            case UPI:
                return Math.random() > 0.2;  // 80% success rate
            case NET_BANKING:
                return Math.random() > 0.3;  // 70% success rate
            case WALLET:
                return Math.random() > 0.1;  // 90% success rate
            case CASH_ON_DELIVERY:
                return true;  // Always succeeds (payment not collected yet)
            default:
                return false;
        }
    }

    /**
     * Handle successful payment
     */
    private void handlePaymentSuccess(Payment payment) {
        log.info("Payment successful for order: {}", payment.getOrder().getId());

        // Generate transaction ID (in real app, this comes from payment gateway)
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String gatewayRef = "GW-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        // Update payment
        payment.markAsSuccess(transactionId, gatewayRef);
        paymentRepository.save(payment);

        // Update order status
        Order order = payment.getOrder();
        order.confirm();
        orderRepository.save(order);

        log.info("Order {} confirmed after successful payment", order.getId());
    }

    /**
     * Handle failed payment
     */
    private void handlePaymentFailure(Payment payment, Order order) {
        log.warn("Payment failed for order: {}", order.getId());

        // Update payment
        payment.markAsFailed("Payment declined by gateway");
        paymentRepository.save(payment);

        // Cancel order and restore stock
        cancelOrderAndRestoreStock(order);

        log.info("Order {} cancelled and stock restored after payment failure", order.getId());
    }

    /**
     * Cancel order and restore stock (rollback logic)
     */
    private void cancelOrderAndRestoreStock(Order order) {
        // Restore stock for all items
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.restoreStock(item.getQuantity());
            productRepository.save(product);

            log.info("Restored {} units of product: {}",
                    item.getQuantity(), product.getName());
        }

        // Update order status
        order.cancel();
        orderRepository.save(order);
    }

    /**
     * Map Payment entity to PaymentResponse DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .transactionId(payment.getTransactionId())
                .gatewayReference(payment.getGatewayReference())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}