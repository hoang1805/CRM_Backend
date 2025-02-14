package com.example.crm_backend.security;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.AuthService;
import com.example.crm_backend.utils.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthService auth_service;
    private final UserDetailsService user_details_service;

    @Autowired
    public CustomAuthenticationProvider(AuthService auth_service, UserDetailsService user_details_service) {
        this.auth_service = auth_service;
        this.user_details_service = user_details_service;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Sử dụng AuthService để kiểm tra thông tin đăng nhập
        User user = auth_service.login(username, password);
        if (user == null) {
            throw new BadCredentialsException("Username or password is not correct. Please try again");
        }

        // Load UserDetails từ UserDetailsService
        UserDetails userDetails = user_details_service.loadUserByUsername(username);

        // Tạo Authentication object
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
