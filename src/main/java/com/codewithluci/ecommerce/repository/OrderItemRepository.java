package com.codewithluci.ecommerce.repository;

import com.codewithluci.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all items in an order
    List<OrderItem> findByOrderId(Long orderId);

    // Find all orders containing a specific product (for analytics)
    List<OrderItem> findByProductId(Long productId);
}