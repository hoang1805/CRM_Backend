package com.example.crm_backend.controllers;

import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.RelationshipService;
import com.example.crm_backend.services.SourceService;
import com.example.crm_backend.services.TaskService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api")
public class AppController {
    private final UserService user_service;

    private final SourceService source_service;

    private final RelationshipService relationship_service;

    private final AccountService account_service;

    private final TaskService task_service;

    @Autowired
    public AppController(UserService user_service, SourceService source_service, RelationshipService relationship_service, AccountService accountService, TaskService taskService) {
        this.user_service = user_service;
        this.source_service = source_service;
        this.relationship_service = relationship_service;
        account_service = accountService;
        task_service = taskService;
    }

    @GetMapping("/init.load")
    public ResponseEntity<Object> load(){
        Map<String, Object> data = new HashMap<>();

        List<User> users = user_service.getUsers();
        data.put("users", users.stream().map(User::releaseCompact).collect(Collectors.toList()));

        List<Source> sources = source_service.getAll();
        data.put("sources", sources.stream().map(Source::releaseCompact).collect(Collectors.toList()));

        List<Relationship> relationships = relationship_service.getAll();
        data.put("relationships", relationships.stream().map(Relationship::releaseCompact).collect(Collectors.toList()));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/csrf.token")
    public ResponseEntity<Object> getCsrfToken(HttpServletRequest request, HttpServletResponse response){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Cookie csrf_cookie = new Cookie("XSRF-TOKEN", csrf.getToken());
        csrf_cookie.setPath("/");
        response.addCookie(csrf_cookie);

        return ResponseEntity.ok("");
    }

    @GetMapping("/home/info")
    public ResponseEntity<Object> getHomeInfo(HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            return ResponseEntity.ok(Map.of(
                    "accounts", account_service.count(),
                    "completed_task", task_service.getCompletedTask(current_user),
                    "in_progress", task_service.getProgressTask(current_user),
                    "expired", task_service.getExpiredTask(current_user),
                    "upcoming", task_service.getUpcomingTasks(current_user).stream().map(task -> task.release(current_user)).collect(Collectors.toList())
            ));
        } catch (Exception e) {
//            throw new IllegalStateException(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }
}
