package com.example.crm_backend.dtos;

import com.example.crm_backend.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AccountDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    private String phone;

    private String code;

    private Gender gender;

    private String email;

    @JsonProperty("assigned_user_id")
    private Long assigned_user_id;

    private UserDTO user_export = null;

    private String job;

    @JsonProperty("source_id")
    private Long source_id;

    @JsonProperty("relationship_id")
    private Long relationship_id;

    @JsonProperty("birthday")
    private Long birthday;

    private Long creator_id;

    private UserDTO creator_export = null;

    private Long created_at;

    @JsonProperty("referrer_id")
    private Long referrer_id;

    private Long last_update;

    private Map<String, Boolean> acl = new HashMap<>();

    public AccountDTO(Long id, String name, String phone, String code, Gender gender, String email, Long assigned_user_id, String job, Long source_id, Long relationship_id, Long birthday, Long creator_id, Long created_at, Long referrer_id, Long last_update) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.code = code;
        this.gender = gender;
        this.email = email;
        this.assigned_user_id = assigned_user_id;
        this.job = job;
        this.source_id = source_id;
        this.relationship_id = relationship_id;
        this.birthday = birthday;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.referrer_id = referrer_id;
        this.last_update = last_update;
    }

    public AccountDTO() {
    }

    public Long getId() {
        return id;
    }

    public AccountDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AccountDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getReferrerId() {
        return referrer_id;
    }

    public AccountDTO setReferrerId(Long referrer_id) {
        this.referrer_id = referrer_id;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AccountDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCode() {
        return code;
    }

    public AccountDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public Long getBirthday() {
        return birthday;
    }

    public AccountDTO setBirthday(Long birthday) {
        this.birthday = birthday;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public AccountDTO setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AccountDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getAssignedUserId() {
        return assigned_user_id;
    }

    public AccountDTO setAssignedUserId(Long assigned_user_id) {
        this.assigned_user_id = assigned_user_id;
        return this;
    }

    public String getJob() {
        return job;
    }

    public AccountDTO setJob(String job) {
        this.job = job;
        return this;
    }

    public Long getSourceId() {
        return source_id;
    }

    public AccountDTO setSourceId(Long source_id) {
        this.source_id = source_id;
        return this;
    }

    public Long getRelationshipId() {
        return relationship_id;
    }

    public AccountDTO setRelationshipId(Long relationship_id) {
        this.relationship_id = relationship_id;
        return this;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public AccountDTO setCreatorId(Long creator_id) {
        this.creator_id = creator_id;
        return this;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public AccountDTO setCreatedAt(Long created_at) {
        this.created_at = created_at;
        return this;
    }

    public Long getLastUpdate() {
        return last_update;
    }

    public AccountDTO setLastUpdate(Long last_update) {
        this.last_update = last_update;
        return this;
    }

    public AccountDTO setACL(Map<String, Boolean> acl) {
        this.acl = acl;
        return this;
    }

    public AccountDTO setUserExport(UserDTO user_export) {
        this.user_export = user_export;
        return this;
    }

    public AccountDTO setCreatorExport(UserDTO creator_export) {
        this.creator_export = creator_export;
        return this;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", assigned_user_id=" + assigned_user_id +
                ", job='" + job + '\'' +
                ", source_id=" + source_id +
                ", relationship_id=" + relationship_id +
                ", birthday=" + birthday +
                ", creator_id=" + creator_id +
                ", created_at=" + created_at +
                ", reffer_id=" + referrer_id +
                ", last_update=" + last_update +
                '}';
    }
}
