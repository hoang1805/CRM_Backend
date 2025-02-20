package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.FeedbackDTO;
import com.example.crm_backend.entities.access_token.AccessToken;
import com.example.crm_backend.entities.feedback.Feedback;
import com.example.crm_backend.services.AccessTokenService;
import com.example.crm_backend.services.FeedbackService;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "api/public")
public class PublicController {
    private final FeedbackService feedback_service;

    private final AccessTokenService access_token_service;

    @Autowired
    public PublicController(FeedbackService feedback_service, AccessTokenService access_token_service) {
        this.feedback_service = feedback_service;
        this.access_token_service = access_token_service;
    }

    @GetMapping("/feedback/{token}")
    public ResponseEntity<Object> checkFeedback(@PathVariable("token") String token) {
        AccessToken access_token = access_token_service.getByToken(token);
        if (access_token == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        if (!Objects.equals("feedback", access_token.getAction()) || !Objects.equals("account", access_token.getObjectType())) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        if (access_token.getExpire() < Timer.now()) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping("/feedback/{token}")
    public ResponseEntity<Object> createFeedback(@PathVariable("token") String token, @RequestBody FeedbackDTO dto) {
        AccessToken access_token = access_token_service.getByToken(token);
        if (access_token == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        if (!Objects.equals("feedback", access_token.getAction()) || !Objects.equals("account", access_token.getObjectType())) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        if (access_token.getExpire() < Timer.now()) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid token"));
        }

        try {
            Feedback feedback = feedback_service.create(access_token, dto);
            access_token_service.deleteById(access_token.getId());
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}
