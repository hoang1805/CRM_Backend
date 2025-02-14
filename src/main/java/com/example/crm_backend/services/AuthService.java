package com.example.crm_backend.services;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository user_repository;

    @Autowired
    public AuthService(UserRepository user_repository) {
        this.user_repository = user_repository;
    }

    public User login(String username, String password){
        Optional<User> user_optional = user_repository.findByUsername(username);
        if (user_optional.isEmpty()) {
            return null;
        }

        User user = user_optional.get();
        if (!Encoder.verifyPassword(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    public User getUser(String username){
        return user_repository.findByUsername(username).get();
    }
}
