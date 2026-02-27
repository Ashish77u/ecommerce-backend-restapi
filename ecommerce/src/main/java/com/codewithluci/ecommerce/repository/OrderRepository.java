package com.codewithluci.ecommerce.repository;

import com.codewithluci.ecommerce.entity.Order;
import com.codewithluci.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders by user
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Find orders by user and status
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    // Find order by ID and user (security: users can only see their own orders)
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    // Find pending orders older than X minutes (for cleanup/alert)
    List<Order> findByStatusAndCreatedAtBefore(
            OrderStatus status, LocalDateTime cutoffTime);

    // Get order with items loaded (avoid N+1 query problem)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    // Admin: Get all orders with filters
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}