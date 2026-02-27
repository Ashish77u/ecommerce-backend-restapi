package com.codewithluci.ecommerce.controller;

import com.codewithluci.ecommerce.dto.respone.CategoryResponse;
import com.codewithluci.ecommerce.dto.respone.ProductResponse;
import com.codewithluci.ecommerce.service.CategoryService;
import com.codewithluci.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // ─── PUBLIC PAGES ────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home(Model model) {
        // Get featured products (first 8)
        Pageable pageable = PageRequest.of(0, 8, Sort.by("createdAt").descending());
        Page<ProductResponse> products = productService.getAllActiveProducts(pageable);

        List<CategoryResponse> categories = categoryService.getAllActiveCategories();

        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/products")
    public String productsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponse> products;

        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId, pageable);
        } else if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search, pageable);
        } else {
            products = productService.getAllActiveProducts(pageable);
        }

        List<CategoryResponse> categories = categoryService.getAllActiveCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("searchQuery", search);

        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetailPage(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    // ─── USER PAGES ──────────────────────────────────────────────────────────

    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login?checkout=true";
        }

        model.addAttribute("username", authentication.getName());
        return "checkout";
    }

    @GetMapping("/my-orders")
    public String myOrdersPage(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", authentication.getName());
        return "my-orders";
    }

    // ─── ADMIN PAGES ─────────────────────────────────────────────────────────

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        return "admin/products";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        return "admin/orders";
    }
}