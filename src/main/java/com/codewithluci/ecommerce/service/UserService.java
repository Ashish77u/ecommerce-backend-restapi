package com.codewithluci.ecommerce.service;


import com.codewithluci.ecommerce.dto.request.RegisterRequest;
import com.codewithluci.ecommerce.dto.respone.UserResponse;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
}