package com.codewithluci.ecommerce.repository;

import com.codewithluci.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find active products by category
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    // Find all active products (paginated)
    Page<Product> findByIsActiveTrue(Pageable pageable);

    // Search by name (case-insensitive)
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(
            String name, Pageable pageable);

    // Find by SKU
    Optional<Product> findBySku(String sku);

    // Check SKU exists
    Boolean existsBySku(String sku);

    // Find products with low stock (for admin alerts)
    List<Product> findByStockQuantityLessThanAndIsActiveTrue(Integer threshold);

    // Find products in price range
    Page<Product> findByPriceBetweenAndIsActiveTrue(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Custom JPQL query - update stock directly (efficient, no entity load)
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int reduceStock(@Param("productId") Long productId,
                    @Param("quantity") int quantity);
}
