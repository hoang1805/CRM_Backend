package com.example.crm_backend.entities.user;

import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class UserACL {
    private final User current_user;

    public UserACL(User user){
        this.current_user = user;
    }

    public boolean canView(User user){
        if (user.getRole() == Role.SUPER_ADMIN) {
            return true;
        }

        return Objects.equals(current_user.getSystemId(), user.getSystemId());
    }

    public boolean canEdit(User user){
        Role role = user.getRole();
        if (role == Role.SUPER_ADMIN) {
            return true;
        }

        if (role == Role.ADMIN && Objects.equals(current_user.getSystemId(), user.getSystemId())) {
            return true;
        }

        return Objects.equals(this.current_user.getId(), user.getId());
    }

    public boolean canDelete(User user) {
        if (user.getRole() == Role.SUPER_ADMIN && current_user.getRole() != Role.SUPER_ADMIN) {
            return true;
        }

        if (user.getRole() == Role.ADMIN && current_user.getRole() != Role.ADMIN && current_user.getRole() != Role.SUPER_ADMIN && Objects.equals(current_user.getSystemId(), user.getSystemId())) {
            return true;
        }

        return false;
    }
}
