package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.FeedbackDTO;
import com.example.crm_backend.entities.access_token.AccessToken;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.feedback.Feedback;
import com.example.crm_backend.services.AccessTokenService;
import com.example.crm_backend.services.FeedbackService;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "api/public")
public class PublicController {
    private final FeedbackService feedback_service;

    private final AccessTokenService access_token_service;

    private final AccountService account_service;

    private final NotificationService notification_service;

    @Autowired
    public PublicController(FeedbackService feedback_service, AccessTokenService access_token_service, AccountService accountService, NotificationService notificationService) {
        this.feedback_service = feedback_service;
        this.access_token_service = access_token_service;
        account_service = accountService;
        notification_service = notificationService;
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

    private Map<String, String> getContext(Feedback feedback) {
        Map<String, String> context = new HashMap<>();

        if (Objects.equals(feedback.getObjectType(), "account")) {
            Account account = account_service.getAccount(feedback.getObjectId());
            if (account == null) {
                return context;
            }

            context.put("type", "account");
            context.put("object_name", account.getName());
            context.put("creator", String.valueOf(account.getCreatorId()));
            context.put("assigner", String.valueOf(account.getAssignedUserId()));
            context.put("referrer", String.valueOf(account.getReferrerId()));
        }

        return context;
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
            Map<String, String> context = getContext(feedback);
            if (context.get("type").equals("account")) {
                List<Long> ids = List.of(Long.valueOf(context.get("creator")), Long.valueOf(context.get("assigner")), Long.valueOf(context.get("referrer")));
                notification_service.systemNotify(ids, "${object_name} sent a new feedback that you followed", context.get("object_name"), feedback.getLink(), feedback.getSystemId());
            }

            access_token_service.deleteById(access_token.getId());
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}
