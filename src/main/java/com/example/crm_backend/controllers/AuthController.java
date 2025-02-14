package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.LoginRequest;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.AuthService;
import com.example.crm_backend.utils.Validator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api")
public class AuthController {
    private final AuthService auth_service;

    private final AuthenticationManager authentication_manager;

    @Autowired
    public AuthController(AuthService auth_service, AuthenticationManager authentication_manager) {
        this.auth_service = auth_service;
        this.authentication_manager = authentication_manager;
    }

    @PostMapping( "/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest login_request, HttpServletRequest request, HttpServletResponse response) {
        String username = login_request.getUsername();
        String password = login_request.getPassword();

        Map<String, Object> body = new HashMap<>();

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            body.put("message", "Username or password is empty. Please try again");
            body.put("status", "fail");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        try {
            Authentication authentication = authentication_manager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = auth_service.getUser(username);

            HttpSession session = request.getSession();
            session.setAttribute("user_id", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            Cookie csrf_cookie = new Cookie("XSRF-TOKEN", csrf.getToken());
            csrf_cookie.setPath("/");
            response.addCookie(csrf_cookie);

            body.put("user", user.release());
            body.put("status", "success");
            return ResponseEntity.ok(body);
        } catch (BadCredentialsException e) {
            // Xử lý sai thông tin đăng nhập
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password. Please try again", "status", "fail"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No active session found", "status", "fail"));
        }

        session.invalidate();
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Cookie csrf_cookie = new Cookie("XSRF-TOKEN", null);
        csrf_cookie.setPath("/");
        csrf_cookie.setMaxAge(0);
        response.addCookie(csrf_cookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
