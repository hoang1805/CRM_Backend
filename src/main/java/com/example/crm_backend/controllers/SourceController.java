package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.SourceDTO;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.SourceService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/source")
public class SourceController {
    private final SourceService source_service;

    private final UserService user_service;

    private final NotificationService notification_service;

    @Autowired
    public SourceController(SourceService source_service, UserService user_service, NotificationService notificationService) {
        this.source_service = source_service;
        this.user_service = user_service;
        notification_service = notificationService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getSources(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (current_user.getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.ok(source_service.getAll().stream().map(source -> source.release(current_user)).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(source_service.getAllBySystemId(current_user.getSystemId()).stream().map(source -> source.release(current_user)).collect(Collectors.toList()));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody SourceDTO source_DTO, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!Objects.equals(current_user.getRole(), Role.ADMIN) && !Objects.equals(current_user.getRole(), Role.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        Source source = source_service.create(source_DTO, current_user);
        notification_service.notifyAll(current_user, new ArrayList<>(),"${user} created new Source ${object_name}", Map.of("object_name", source.getName()), source.getLink(), source.getSystemId());

        return ResponseEntity.ok(Map.of("source", source.release(current_user)));
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> edit(@PathVariable("id") Long id, @RequestBody SourceDTO source_DTO,HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Source current_source = source_service.getSource(id);
        if (current_source == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid source"));
        }

        if (!Objects.equals(current_source.getSystemId(), current_user.getSystemId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid source"));
        }

        if (!current_source.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Source source = source_service.edit(id, source_DTO);
//            notification_service.notify(current_user, List.of(source.getCreatorId()), "${user} edited Source ${object_name}", source.getName(), source.getLink());
            notification_service.notifyAll(current_user, new ArrayList<>(),"${user} edited Source ${object_name}", Map.of("object_name", source.getName()), source.getLink(), source.getSystemId());

            return ResponseEntity.ok(Map.of("source", source.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Source current_source = source_service.getSource(id);
        if (current_source == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid source"));
        }

        if (!Objects.equals(current_source.getSystemId(), current_user.getSystemId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid source"));
        }

        if (!current_source.acl().canDelete(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            source_service.delete(id);
//            notification_service.notify(current_user, List.of(current_source.getCreatorId()), "${user} deleted Source ${object_name}", current_source.getName());
            notification_service.notifyAll(current_user, new ArrayList<>(),"${user} deleted new Source ${object_name}", Map.of("object_name", current_source.getName()), "", current_source.getSystemId());
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String query, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        return ResponseEntity.ok(source_service.search(query, current_user)
                .stream().map(Source::releaseCompact).collect(Collectors.toList()));
    }
}
