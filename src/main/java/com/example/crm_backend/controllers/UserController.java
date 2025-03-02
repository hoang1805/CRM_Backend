package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.UserPasswordDTO;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list")
    public ResponseEntity<Object> getUsers(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

//        System.out.println("ipp: " + ipp + ", page: " + page);
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.ok(user_service.paginate(ipp, page).map(user -> user.release(current_user)));
        }
        return ResponseEntity.ok(user_service.paginateBySystem(ipp, page, current_user.getSystemId()).map(user -> user.release(current_user)));
    }

    @GetMapping("")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(Map.of("user", current_user.release()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!user.acl().canView(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            User new_user = user_service.createUser(user, current_user);
            return ResponseEntity.ok(Map.of("user", new_user.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            User target_user = user_service.getUserBySystem(id, current_user.getSystemId());
            if (current_user.getRole() == Role.SUPER_ADMIN) {
                target_user = user_service.getUser(id);
            }

            if (!target_user.acl().canDelete(current_user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
            }

            user_service.deleteUser(id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUsers(@RequestParam String query, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(user_service.searchUsers(query, current_user)
                .stream().map(User::releaseCompact).collect(Collectors.toList()));
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> editUser(@RequestBody UserDTO user_DTO, @PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if(!user.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            User edited = user_service.updateUser(id, user_DTO);
            return ResponseEntity.ok(Map.of("user", edited.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/edit.password/{id}")
    public ResponseEntity<Object> editUserPassword(@RequestBody UserPasswordDTO userPassword_DTO, @PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if(!user.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            User edited = user_service.updateUserPassword(id, userPassword_DTO);
            return ResponseEntity.ok(Map.of("user", edited.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/grant/manager/{id}")
    public ResponseEntity<Object> grantManager(@PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!user.acl().canEdit(current_user) || Objects.equals(user.getRole(), Role.ADMIN) || Objects.equals(user.getRole(), Role.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            user = user_service.grantManager(id);
            return ResponseEntity.ok(Map.of("user", user.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/grant/staff/{id}")
    public ResponseEntity<Object> grantStaff(@PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!user.acl().canEdit(current_user) || Objects.equals(user.getRole(), Role.ADMIN) || Objects.equals(user.getRole(), Role.STAFF)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            user = user_service.grantStaff(id);
            return ResponseEntity.ok(Map.of("user", user.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/reset.password/{id}")
    public ResponseEntity<Object> resetPassword(@PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        User user = user_service.getUserBySystem(id, current_user.getSystemId());
        if (current_user.getRole() == Role.SUPER_ADMIN) {
            user = user_service.getUser(id);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!user.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            user = user_service.resetPassword(id);
            return ResponseEntity.ok(Map.of("user", user.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }
}
