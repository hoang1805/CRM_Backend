package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;
import java.util.Map;

public class RelationshipDTO {

    @JsonProperty("id")
    private Long id;

    private String name;

    private String color;

    private String description;

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    private Map<String, Boolean> acl = new HashMap<>();

    public RelationshipDTO() {
    }

    public RelationshipDTO(Long id, String name, String color, String description, Long creator_id, Long created_at, Long last_update) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Long getId() {
        return id;
    }

    public RelationshipDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RelationshipDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public RelationshipDTO setColor(String color) {
        this.color = color;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RelationshipDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public RelationshipDTO setCreatorId(Long creator_id) {
        this.creator_id = creator_id;
        return this;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public RelationshipDTO setCreatedAt(Long created_at) {
        this.created_at = created_at;
        return this;
    }

    public Long getLastUpdate() {
        return last_update;
    }

    public RelationshipDTO setLastUpdate(Long last_update) {
        this.last_update = last_update;
        return this;
    }

    public RelationshipDTO setAcl(Map<String, Boolean> acl) {
        this.acl = acl;
        return this;
    }
}
