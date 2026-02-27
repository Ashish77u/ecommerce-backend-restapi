package com.codewithluci.ecommerce.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String token;
    private String type = "Bearer";  // OAuth 2.0 standard
    private Long userId;
    private String username;
    private String email;
    private String role;
}
