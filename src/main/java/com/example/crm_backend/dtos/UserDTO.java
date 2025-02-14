package com.example.crm_backend.dtos;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.enums.Role;

import java.util.HashMap;
import java.util.Map;

public class UserDTO {
    private Long id;

    private String username;

    private String name;

    private String phone;

    private String email;

    private Long birthday;

    private Gender gender;

    private String title;

    private Role role;

    private String sign;

    private Long creator_id;

    private String creator_name;

    private Long last_update;

    private Long created_at;

    private Map<String, Boolean> acl = new HashMap<>();

    public UserDTO(Long id, String username, String name, String phone, String email, Long birthday, Gender gender, String title, Role role, String sign, Long creator_id, String creator_name, Long last_update, Long created_at) {
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
        this.creator_id = creator_id;
        this.creator_name = creator_name;
        this.last_update = last_update;
        this.created_at = created_at;
    }

    public UserDTO() {

    }

    public Long getId() {
        return id;
    }

    public UserDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Long getBirthday() {
        return birthday;
    }

    public UserDTO setBirthday(Long birthday) {
        this.birthday = birthday;
        return this;
    }

    public UserDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public UserDTO setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public UserDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public UserDTO setRole(Role role) {
        this.role = role;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public UserDTO setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public UserDTO setCreatorId(Long creator_id) {
        this.creator_id = creator_id;
        return this;
    }

    public String getCreatorName() {
        return creator_name;
    }

    public UserDTO setCreatorName(String creator_name) {
        this.creator_name = creator_name;
        return this;
    }

    public Long getLastUpdate() {
        return last_update;
    }

    public UserDTO setLastUpdate(Long last_update) {
        this.last_update = last_update;
        return this;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public Map<String, Boolean> getACL() {
        return acl;
    }

    public UserDTO setACL(Map<String, Boolean> acl) {
        this.acl = acl;
        return this;
    }

    public UserDTO setCreatedAt(Long created_at) {
        this.created_at = created_at;
        return this;
    }
}
