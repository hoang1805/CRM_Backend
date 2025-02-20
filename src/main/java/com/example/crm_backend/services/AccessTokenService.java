package com.example.crm_backend.services;

import com.example.crm_backend.entities.Exportable;
import com.example.crm_backend.entities.access_token.AccessToken;
import com.example.crm_backend.repositories.AccessTokenRepository;
import com.example.crm_backend.utils.Timer;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService {
    private final AccessTokenRepository access_token_repository;

    @Autowired
    public AccessTokenService(AccessTokenRepository access_token_repository) {
        this.access_token_repository = access_token_repository;
    }

    public AccessToken create(Exportable exportable, long expire, String action) {
        AccessToken access_token = new AccessToken(exportable, expire, action);
        if (access_token_repository.existsByObjectIdAndObjectTypeAndActionAndExpireGreaterThan(access_token.getObjectId(), access_token.getObjectType(), access_token.getAction(), Timer.now())) {
            throw new EntityExistsException("Already exists");
        }

        return access_token_repository.save(access_token);
    }

    public AccessToken getByToken(String token) {
        return access_token_repository.findByToken(token);
    }

    public void deleteById(Long id) {
        access_token_repository.deleteById(id);
    }
}
