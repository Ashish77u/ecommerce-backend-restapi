package com.codewithluci.ecommerce.service;



import com.codewithluci.ecommerce.dto.request.CategoryRequest;
import com.codewithluci.ecommerce.dto.respone.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryBySlug(String slug);
    List<CategoryResponse> getAllActiveCategories();
    List<CategoryResponse> getAllCategories();
    void deleteCategory(Long id);
}