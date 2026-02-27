package com.codewithluci.ecommerce.security;

import com.codewithluci.ecommerce.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().startsWith("/swagger-ui") ||
                request.getServletPath().startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                if (!jwtUtil.validateToken(jwt)) {
                    throw new JwtAuthenticationException("Invalid or expired JWT token");
                }

                String username = jwtUtil.extractUsername(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set authentication for user: {}", username);
                }
            }
        } catch (JwtAuthenticationException ex) {
            log.error("JWT authentication failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"" + ex.getMessage() + "\",\"timestamp\":\"" +
                            java.time.LocalDateTime.now() + "\"}"
            );
            return;  // Don't continue filter chain
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }

        return null;
    }
}
/*
        ---

        ## **Filter Logic Breakdown**

        ### **Request Flow Through Filter**
        ```
        1. Extract Authorization header
   └─ "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

           2. Remove "Bearer " prefix
   └─ "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

           3. Validate token structure + signature
   └─ Check expiration
   └─ Verify signature with secret key

4. Extract username from token
   └─ "johndoe"

           5. Load user from database
   └─ Get authorities (ROLE_USER)

6. Create Authentication object
   └─ UsernamePasswordAuthenticationToken

7. Set in SecurityContext
   └─ SecurityContextHolder.getContext().setAuthentication(...)

8. Continue to controller
   └─ @PreAuthorize annotations now work



 */