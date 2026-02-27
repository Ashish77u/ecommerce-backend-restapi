package com.codewithluci.ecommerce.controller;

import com.codewithluci.ecommerce.dto.request.OrderRequest;
import com.codewithluci.ecommerce.dto.respone.ApiResponse;
import com.codewithluci.ecommerce.dto.respone.OrderResponse;
import com.codewithluci.ecommerce.dto.respone.UserResponse;
import com.codewithluci.ecommerce.entity.OrderStatus;
import com.codewithluci.ecommerce.service.OrderService;
import com.codewithluci.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;  // ✅ Inject UserService


    // ─── USER ENDPOINTS ──────────────────────────────────────────────────────

    /**
     * Create new order (authenticated users only)
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        log.info("Order creation request from: {}", authentication.getName());

        // Extract user ID from JWT token
        Long userId = getUserIdFromAuthentication(authentication);

        OrderResponse order = orderService.createOrder(request, userId);

        return new ResponseEntity<>(
                ApiResponse.success("Order created successfully", order),
                HttpStatus.CREATED
        );
    }

    /**
     * Get user's own orders (paginated)
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<OrderResponse> orders = orderService.getUserOrders(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Orders retrieved successfully", orders)
        );
    }

    /**
     * Get specific order by ID (user can only see their own orders)
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        OrderResponse order = orderService.getOrderById(orderId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Order retrieved successfully", order)
        );
    }

    /**
     * Cancel order (user can only cancel their own orders)
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        log.info("Cancel order request for order: {}", orderId);

        Long userId = getUserIdFromAuthentication(authentication);

        OrderResponse order = orderService.cancelOrder(orderId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled successfully", order)
        );
    }

    // ─── ADMIN ENDPOINTS ─────────────────────────────────────────────────────

    /**
     * Get all orders (admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orders = orderService.getAllOrders(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("All orders retrieved successfully", orders)
        );
    }

    /**
     * Get orders by status (admin only)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OrderResponse> orders = orderService.getOrdersByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders with status " + status + " retrieved successfully",
                        orders
                )
        );
    }

    /**
     * Update order status (admin only)
     */
    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        log.info("Admin updating order {} to status: {}", orderId, status);

        OrderResponse order = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(
                ApiResponse.success("Order status updated successfully", order)
        );
    }

    // ─── HELPER METHODS ──────────────────────────────────────────────────────

    /**
     * Extract user ID from authentication token
     * In real app, you'd fetch user from database by username
     * For now, we'll add this to UserService
     */
//    private Long getUserIdFromAuthentication(Authentication authentication) {
//        // Get username from JWT token
//        String username = authentication.getName();
//
//        // In production, fetch user from database
//        // For now, we'll throw exception if username doesn't exist
//        // You should inject UserRepository and fetch actual user ID
//
//        // TEMPORARY: You need to implement this properly
//        // Option 1: Store user ID in JWT claims
//        // Option 2: Fetch user from database each time (simple but slower)
//
//        // For now, let's assume we store it in JWT custom claims
//        // We'll fix this in next step
//        return 1L; // PLACEHOLDER - we'll fix this
//    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getUserByUsername(username);
        return user.getId();
    }
}
