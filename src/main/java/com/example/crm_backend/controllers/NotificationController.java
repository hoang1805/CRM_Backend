package com.example.crm_backend.controllers;

import com.example.crm_backend.entities.notification.Notification;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {
    private final NotificationService notification_service;

    private final UserService user_service;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        notification_service = notificationService;
        user_service = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getNotifications(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(notification_service.paginate(ipp, page, current_user).map(Notification::release));
    }

    @GetMapping("/count/unread")
    public ResponseEntity<Object> countUnread(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(Map.of("count", notification_service.countByUser(current_user)));
    }

    @PostMapping("/mark.read/{id}")
    public ResponseEntity<Object> markRead(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            Notification notification = notification_service.markAsRead(current_user, id);
            return ResponseEntity.ok(Map.of("notification", notification.release()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/mark.all")
    public ResponseEntity<Object> markAllRead(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            notification_service.markAllAsRead(current_user);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }
}
