package com.codewithluci.ecommerce.controller;

import com.codewithluci.ecommerce.dto.respone.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> userAccess() {
        ApiResponse<String> response = ApiResponse.success(
                "User content accessed",
                "This is user-level content"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminAccess() {
        ApiResponse<String> response = ApiResponse.success(
                "Admin content accessed",
                "This is admin-level content"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<String>> allAccess() {
        ApiResponse<String> response = ApiResponse.success(
                "Public content accessed",
                "This is public content"
        );
        return ResponseEntity.ok(response);
    }
}