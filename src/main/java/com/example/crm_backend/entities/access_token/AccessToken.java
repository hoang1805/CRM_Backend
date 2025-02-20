package com.example.crm_backend.entities.access_token;

import com.example.crm_backend.entities.Exportable;
import com.example.crm_backend.utils.Timer;
import com.example.crm_backend.utils.Token;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "AccessTokens")
public class AccessToken {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String token;

    private Long expire;

    private String action;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "created_at")
    private Long createdAt;

    public AccessToken() {
    }

    public AccessToken(Long id, String token, Long expire, String action, Long objectId, String objectType, Long createdAt) {
        this.id = id;
        this.token = token;
        this.expire = expire;
        this.action = action;
        this.objectId = objectId;
        this.objectType = objectType;
        this.createdAt = createdAt;
    }

    public AccessToken(Exportable exportable, Long expire, String action) {
        Map<String, Object> map = exportable.export();
        this.objectId = (Long) map.get("id");
        this.objectType = (String) map.get("name");
        this.token = Token.generateToken();
        this.createdAt = Timer.now();
        this.expire = Timer.now() + expire;
        this.action = action;
    }


}
