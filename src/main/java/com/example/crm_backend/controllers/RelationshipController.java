package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.RelationshipDTO;
import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.RelationshipService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/relationship")
public class RelationshipController {
    private final RelationshipService relationship_service;

    private final UserService user_service;

    private final NotificationService notification_service;

    @Autowired
    public RelationshipController(RelationshipService relationship_service, UserService user_service, NotificationService notificationService) {
        this.relationship_service = relationship_service;
        this.user_service = user_service;
        notification_service = notificationService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getRelationships(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (current_user.getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.ok(relationship_service.getAll().stream().map(relationship -> relationship.release(current_user)).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(relationship_service.getAllBySystemId(current_user.getSystemId()).stream().map(relationship -> relationship.release(current_user)).collect(Collectors.toList()));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody RelationshipDTO relationship_DTO, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!Objects.equals(current_user.getRole(), Role.ADMIN) && !Objects.equals(current_user.getRole(), Role.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        Relationship relationship = relationship_service.create(relationship_DTO, current_user);

        return ResponseEntity.ok(Map.of("relationship", relationship.release(current_user)));
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> edit(@PathVariable("id") Long id, @RequestBody RelationshipDTO relationship_DTO, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!relationship_service.isExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid relationship"));
        }

        Relationship current_relationship = relationship_service.getRelationship(id);
        if (!current_relationship.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Relationship relationship = relationship_service.edit(id, relationship_DTO);
            notification_service.notify(current_user, List.of(relationship.getCreatorId()),  "${user} edited Relationship ${object_name}", relationship.getName(), relationship.getLink());

            return ResponseEntity.ok(Map.of("relationship", relationship.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/edit.color/{id}")
    public ResponseEntity<Object> editColor(@PathVariable("id") Long id, @RequestBody RelationshipDTO relationship_DTO, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!relationship_service.isExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid relationship"));
        }

        Relationship current_relationship = relationship_service.getRelationship(id);
        if (!current_relationship.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Relationship relationship = relationship_service.editColor(id, relationship_DTO);
            notification_service.notify(current_user, List.of(relationship.getCreatorId()),  "${user} edited Relationship ${object_name}", relationship.getName(), relationship.getLink());

            return ResponseEntity.ok(Map.of("relationship", relationship.release(current_user)));
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

        if (!relationship_service.isExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid relationship"));
        }

        Relationship current_relationship = relationship_service.getRelationship(id);
        if (!current_relationship.acl().canDelete(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            relationship_service.delete(id);
            notification_service.notify(current_user, List.of(current_relationship.getCreatorId()),  "${user} deleted Relationship ${object_name}", current_relationship.getName());

            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }
}
