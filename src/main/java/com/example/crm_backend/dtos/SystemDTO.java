package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class SystemDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    @JsonProperty("max_user")
    private Long maxUser;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    private Map<String, Boolean> acl = new HashMap<>();


    public SystemDTO(Long id, String name, Long maxUser, Long createdAt, Long lastUpdate) {
        this.id = id;
        this.name = name;
        this.maxUser = maxUser;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public SystemDTO() {

    }
}
