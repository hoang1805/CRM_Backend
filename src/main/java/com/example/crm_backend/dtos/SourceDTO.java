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
public class SourceDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    private String code;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("system_id")
    private Long systemId;

    private Map<String, Boolean> acl = new HashMap<>();

    public SourceDTO() {
    }

    public SourceDTO(Long id, String name, String code, Long parentId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }
}
