package com.example.crm_backend.entities.user;

import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class UserACL {
    private final User user;

    public UserACL(User user){
        this.user = user;
    }

    public boolean canView(User user){
        return true;
    }

    public boolean canEdit(User user){
        return Objects.equals(this.user.getId(), user.getId()) || Objects.equals(user.getRole(), Role.ADMIN);
    }

    public boolean canDelete(User user){
        return !Objects.equals(this.user.getRole(), Role.ADMIN) && Objects.equals(user.getRole(), Role.ADMIN);
    }
}
