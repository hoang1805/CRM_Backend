package com.example.crm_backend.dtos;


import java.util.HashMap;
import java.util.Map;


public class UserPasswordDTO {
    private Long id;

    private String oldPassword;

    private String newPassword;

    private Map<String, Boolean> acl = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Map<String, Boolean> getAcl() {
        return acl;
    }

    public void setAcl(Map<String, Boolean> acl) {
        this.acl = acl;
    }

    public UserPasswordDTO(Long id, String oldPassword, String newPassword, Map<String, Boolean> acl) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.acl = acl;
    }
}
