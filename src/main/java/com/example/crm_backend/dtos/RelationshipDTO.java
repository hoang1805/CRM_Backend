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
public class RelationshipDTO {

    @JsonProperty("id")
    private Long id;

    private String name;

    private String color;

    private String description;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("system_id")
    private Long systemId;

    private Map<String, Boolean> acl = new HashMap<>();

    public RelationshipDTO() {
    }

    public RelationshipDTO(Long id, String name, String color, String description, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }
}
