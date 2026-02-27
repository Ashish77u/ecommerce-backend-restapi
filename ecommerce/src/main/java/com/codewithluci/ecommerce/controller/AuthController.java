package com.codewithluci.ecommerce.controller;

import com.codewithluci.ecommerce.dto.request.LoginRequest;
import com.codewithluci.ecommerce.dto.request.RegisterRequest;
import com.codewithluci.ecommerce.dto.respone.ApiResponse;
import com.codewithluci.ecommerce.dto.respone.JwtResponse;
import com.codewithluci.ecommerce.dto.respone.UserResponse;
import com.codewithluci.ecommerce.security.AuthService;
import com.codewithluci.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration and authentication endpoints")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with USER role"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error or user already exists"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration request received for username: {}", request.getUsername());

        UserResponse userResponse = userService.registerUser(request);

        ApiResponse<UserResponse> response = ApiResponse.success(
                "User registered successfully",
                userResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns JWT token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login request received for: {}", request.getUsernameOrEmail());

        JwtResponse jwtResponse = authService.login(request);

        ApiResponse<JwtResponse> response = ApiResponse.success(
                "Login successful",
                jwtResponse
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Returns currently authenticated user information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User info retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            Authentication authentication) {

        log.info("Fetching current user info for: {}", authentication.getName());

        UserResponse userResponse = userService.getUserByUsername(authentication.getName());

        ApiResponse<UserResponse> response = ApiResponse.success(
                "User info retrieved successfully",
                userResponse
        );

        return ResponseEntity.ok(response);
    }
}




//package com.codewithluci.ecommerce.controller;
//
//
//import com.codewithluci.ecommerce.dto.request.LoginRequest;
//import com.codewithluci.ecommerce.dto.request.RegisterRequest;
//import com.codewithluci.ecommerce.dto.respone.ApiResponse;
//import com.codewithluci.ecommerce.dto.respone.JwtResponse;
//import com.codewithluci.ecommerce.dto.respone.UserResponse;
//import com.codewithluci.ecommerce.security.AuthService;
//import com.codewithluci.ecommerce.service.UserService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//@Slf4j
//public class AuthController {
//
//    private final UserService userService;
//    private final AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
//            @Valid @RequestBody RegisterRequest request) {
//
//        log.info("Registration request received for username: {}", request.getUsername());
//
//        UserResponse userResponse = userService.registerUser(request);
//
//        ApiResponse<UserResponse> response = ApiResponse.success(
//                "User registered successfully",
//                userResponse
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<JwtResponse>> login(
//            @Valid @RequestBody LoginRequest request) {
//
//        log.info("Login request received for: {}", request.getUsernameOrEmail());
//
//        JwtResponse jwtResponse = authService.login(request);
//
//        ApiResponse<JwtResponse> response = ApiResponse.success(
//                "Login successful",
//                jwtResponse
//        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Get current authenticated user info
//     */
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
//        log.info("Fetching current user info for: {}", authentication.getName());
//
//        UserResponse userResponse = userService.getUserByUsername(authentication.getName());
//
//        ApiResponse<UserResponse> response = ApiResponse.success(
//                "User info retrieved successfully",
//                userResponse
//        );
//
//        return ResponseEntity.ok(response);
//    }
//}