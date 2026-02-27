package com.codewithluci.ecommerce.security;


import com.codewithluci.ecommerce.dto.request.LoginRequest;
import com.codewithluci.ecommerce.dto.respone.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
}