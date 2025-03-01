package com.example.crm_backend.dtos;

import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class UserDTO {
    private Long id;

    private String username;

    private String name;

    private String phone;

    private String email;

    @JsonProperty("birthday")
    private Long birthday;

    private Gender gender;

    private String title;

    private Role role;

    private String sign;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("system_id")
    private Long systemId;

    private Map<String, Boolean> acl = new HashMap<>();

    public UserDTO(Long id, String username, String name, String phone, String email, Long birthday, Gender gender, String title, Role role, String sign, Long creatorId, String creatorName, Long lastUpdate, Long createdAt, Long systemId) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.title = title;
        this.role = role;
        this.sign = sign;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.lastUpdate = lastUpdate;
        this.createdAt = createdAt;
        this.systemId = systemId;
    }

    public UserDTO() {

    }
}
