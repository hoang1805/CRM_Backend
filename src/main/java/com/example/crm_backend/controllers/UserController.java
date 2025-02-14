package com.example.crm_backend.controllers;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.entities.user.UserValidator;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/user")
public class UserController {
    private final UserService user_service;

    @Autowired
    public UserController(UserService user_service) {
        this.user_service = user_service;
    }

    @GetMapping("/get.list")
    public ResponseEntity<Object> getUsers(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<UserDTO> data = user_service.paginate(ipp, page).map(user -> user.release(current_user));
        return ResponseEntity.ok(Map.of("data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUser(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!user.acl().canView(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
        }

        return ResponseEntity.ok(Map.of("user", user.release(current_user)));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody User user, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!Objects.equals(current_user.getRole(), Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
        }

        try {
            User new_user = user_service.createUser(user);
            return ResponseEntity.ok(Map.of("user", new_user.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            User target_user = user_service.getUser(id);
            if (!target_user.acl().canDelete(current_user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
            }

            user_service.deleteUser(id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUsers(@RequestParam String query, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(user_service.searchUsers((query))
                .stream().map(User::releaseCompact).collect(Collectors.toList()));
    }
}
