package com.codewithluci.ecommerce.security.impl;


import com.codewithluci.ecommerce.dto.request.LoginRequest;
import com.codewithluci.ecommerce.dto.respone.JwtResponse;
import com.codewithluci.ecommerce.entity.User;
import com.codewithluci.ecommerce.exception.InvalidCredentialsException;
import com.codewithluci.ecommerce.repository.UserRepository;
import com.codewithluci.ecommerce.security.AuthService;
import com.codewithluci.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);

            // Fetch user entity for additional info
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                    .orElseThrow(() -> new InvalidCredentialsException("User not found"));

            log.info("User logged in successfully: {}", user.getUsername());

            // Build response
            return JwtResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for: {}", request.getUsernameOrEmail());
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}
