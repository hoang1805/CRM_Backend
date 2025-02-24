package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.FeedbackDTO;
import com.example.crm_backend.entities.access_token.AccessToken;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.feedback.Feedback;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.*;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.timer.Timer;
import java.util.Map;

@RestController
@RequestMapping(path = "api/feedback")
public class FeedbackController {
    private final UserService user_service;

    private final FeedbackService feedback_service;

    private final EmailService email_service;

    private final AccessTokenService access_token_service;

    private final AccountService account_service;

    @Autowired
    public FeedbackController(UserService user_service, FeedbackService feedback_service, EmailService email_service, AccessTokenService access_token_service, AccountService account_service) {
        this.user_service = user_service;
        this.feedback_service = feedback_service;
        this.email_service = email_service;
        this.access_token_service = access_token_service;
        this.account_service = account_service;
    }

    @PostMapping("/request.account/{id}")
    public ResponseEntity<Object> requestFeedback(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid user"));
        }

        Account account = account_service.getAccount(id);
        if (account == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Account not found"));
        }

        if (!account.acl().canView(current_user)) {
            return ResponseEntity.status(403).body(Map.of("message", "You do not have access"));
        }

        if (account.getEmail() == null || account.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", "Can not find account email"));
        }

        String email = account.getEmail();
        AccessToken token = access_token_service.create(account, Timer.ONE_DAY, "feedback");
        try{
            email_service.sendEmail(email, "Đánh giá trải nghiệm", feedback_service.getFeedbackContent(token.getToken()));
            return ResponseEntity.ok("Request feedback successful");
        } catch (EntityExistsException existsException) {
            access_token_service.deleteById(token.getId());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "You have already requested feedback"));
        } catch (Exception e) {
            access_token_service.deleteById(token.getId());
//            throw new IllegalStateException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/list/{account_id}")
    public ResponseEntity<Object> search(@RequestParam String query, @RequestParam Long start, @RequestParam Long end, @RequestParam Long page, @RequestParam Long ipp, HttpServletRequest request, @PathVariable String account_id){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<Feedback> feedbacks = feedback_service.paginateByAccount(Math.toIntExact(ipp), Math.toIntExact(page), account_id, query, start, end);
        Page<FeedbackDTO> data = feedbacks.map(feedback -> feedback.release(current_user));

        return ResponseEntity.ok(data);
    }
}
