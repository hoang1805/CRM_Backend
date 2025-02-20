package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.access_token.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    AccessToken findByToken(String token);

    boolean existsByToken(String token);

    boolean existsByObjectIdAndObjectTypeAndActionAndExpireGreaterThan(Long object_id, String object_type, String action, Long expire_time);
}
