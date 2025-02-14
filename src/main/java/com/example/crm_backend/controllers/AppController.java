package com.example.crm_backend.controllers;

import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.SourceService;
import com.example.crm_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public AppController(UserService user_service, SourceService source_service) {
        this.user_service = user_service;
        this.source_service = source_service;
    }

    @GetMapping("/init.load")
    public ResponseEntity<Object> load(){
        Map<String, Object> data = new HashMap<>();

        List<User> users = user_service.getUsers();
        data.put("users", users.stream().map(User::releaseCompact).collect(Collectors.toList()));

        List<Source> sources = source_service.getAll();
        data.put("sources", sources.stream().map(Source::releaseCompact).collect(Collectors.toList()));
        return ResponseEntity.ok(data);
    }
}
