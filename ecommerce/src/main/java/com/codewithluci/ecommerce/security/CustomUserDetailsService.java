package com.codewithluci.ecommerce.security;


import com.codewithluci.ecommerce.entity.User;
import com.codewithluci.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service                  // ✅ Must be @Service so Spring picks it up as UserDetailsService bean
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        log.debug("Loading user by username/email: {}", usernameOrEmail);

        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + usernameOrEmail));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
}

/*
        ---

        ## **Checklist Before Restarting**

Go through each item:
        ```
        ✅ AppConfig.java  → DELETED
✅ SecurityConfig.java → PasswordEncoder @Bean defined HERE
✅ SecurityConfig.java → No @RequiredArgsConstructor on class
✅ UserServiceImpl.java → private final PasswordEncoder passwordEncoder
✅ UserServiceImpl.java → @RequiredArgsConstructor present
✅ CustomUserDetailsService.java → @Service annotation present
}



 */