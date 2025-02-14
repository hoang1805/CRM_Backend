package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class SourceDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    private String code;

    @JsonProperty("parent_id")
    private Long parent_id;

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    private Map<String, Boolean> acl = new HashMap<>();

    public SourceDTO() {
    }

    public SourceDTO(Long id, String name, String code, Long parent_id, Long creator_id, Long created_at, Long last_update) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parent_id = parent_id;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Long getId() {
        return id;
    }

    public SourceDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SourceDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SourceDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public Long getParentId() {
        return parent_id;
    }

    public SourceDTO setParentId(Long parent_id) {
        this.parent_id = parent_id;
        return this;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public SourceDTO setCreatorId(Long creator_id) {
        this.creator_id = creator_id;
        return this;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public SourceDTO setCreatedAt(Long created_at) {
        this.created_at = created_at;
        return this;
    }

    public Long getLastUpdate() {
        return last_update;
    }

    public SourceDTO setLastUpdate(Long last_update) {
        this.last_update = last_update;
        return this;
    }

    public SourceDTO setACL(Map<String, Boolean> acl) {
        this.acl = acl;
        return this;
    }
}
