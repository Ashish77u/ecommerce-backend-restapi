package com.codewithluci.ecommerce.service;

import com.codewithluci.ecommerce.dto.request.OrderRequest;
import com.codewithluci.ecommerce.dto.respone.OrderResponse;
import com.codewithluci.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request, Long userId);
    OrderResponse getOrderById(Long orderId, Long userId);
    Page<OrderResponse> getUserOrders(Long userId, Pageable pageable);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse cancelOrder(Long orderId, Long userId);

    // Admin operations
    Page<OrderResponse> getAllOrders(Pageable pageable);
    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);
}