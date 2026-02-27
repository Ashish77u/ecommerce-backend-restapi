package com.codewithluci.ecommerce.service.impl;

import com.codewithluci.ecommerce.dto.request.OrderItemRequest;
import com.codewithluci.ecommerce.dto.request.OrderRequest;
import com.codewithluci.ecommerce.dto.respone.OrderItemResponse;
import com.codewithluci.ecommerce.dto.respone.OrderResponse;
import com.codewithluci.ecommerce.entity.*;
import com.codewithluci.ecommerce.exception.ResourceNotFoundException;
import com.codewithluci.ecommerce.exception.insufficientStockException.InsufficientStockException;
import com.codewithluci.ecommerce.repository.OrderRepository;
import com.codewithluci.ecommerce.repository.ProductRepository;
import com.codewithluci.ecommerce.repository.UserRepository;
import com.codewithluci.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long userId) {
        log.info("Creating order for user: {}", userId);

        // 1. Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        // 2. Create order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .totalAmount(BigDecimal.ZERO)  // Will be calculated
                .build();

        // 3. Process each order item
        for (OrderItemRequest itemRequest : request.getItems()) {
            // 3a. Fetch product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            // 3b. Validate product is active and in stock
            if (!product.getIsActive()) {
                throw new IllegalStateException(
                        "Product is not available: " + product.getName());
            }
            // Critical Business Logic in This Code.
            if (!product.hasEnoughStock(itemRequest.getQuantity())) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName() +
                                ". Available: " + product.getStockQuantity() +
                                ", Requested: " + itemRequest.getQuantity());
            }

            // 3c. Reduce stock (THIS IS CRITICAL)
            product.reduceStock(itemRequest.getQuantity());
            productRepository.save(product);

            // 3d. Create order item (snapshot of product at this moment)
            OrderItem orderItem = OrderItem.fromProduct(product, itemRequest.getQuantity());
            order.addOrderItem(orderItem);
        }

        // 4. Calculate total amount
        order.calculateTotalAmount();

        // 5. Save order (cascades to order items)
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully with id: {}", savedOrder.getId());

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or access denied"));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order {} to status: {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        order.setStatus(status);
        Order updated = orderRepository.save(order);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        log.info("Cancelling order: {}", orderId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or access denied"));

        // Check if order can be cancelled
        if (!order.canBeCancelled()) {
            throw new IllegalStateException(
                    "Cannot cancel order in status: " + order.getStatus());
        }

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
        Order cancelled = orderRepository.save(order);

        log.info("Order cancelled successfully: {}", orderId);

        return mapToResponse(cancelled);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }

    // ─── MAPPING METHODS ────────────────────────────────────────────────────

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}