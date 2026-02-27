package com.codewithluci.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor  // Spring & JPA ko default constructor chahiye hota hai. Q. why JPA required no-arg contructor. ans Hibernate reflection use karta hai object create karne ke liye.
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // URL-friendly version of name
    // e.g. "Men's Clothing" → "mens-clothing"
    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(nullable = false)
    private Boolean isActive = true;

    // ONE Category has MANY Products
    @OneToMany(
            mappedBy = "category",       // refers to field name in Product entity
            cascade = CascadeType.ALL,   // operations on Category cascade to Products
            fetch = FetchType.LAZY,      // DON'T load products unless explicitly needed
            orphanRemoval = true         // delete products if removed from category
    )
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
