package com.example.crm_backend.entities.source;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class SourceACL {
    private Source source;

    public SourceACL(Source source) {
        this.source = source;
    }

    public boolean canView(User user){
        Role user_role = user.getRole();
        return Objects.equals(user_role, Role.ADMIN) || Objects.equals(user_role, Role.MANAGER);
    }

    public boolean canEdit(User user){
        Role user_role = user.getRole();
        return Objects.equals(user_role, Role.ADMIN) || Objects.equals(user_role, Role.MANAGER);
    }

    public boolean canDelete(User user){
        Role user_role = user.getRole();
        return Objects.equals(user_role, Role.ADMIN) || Objects.equals(user_role, Role.MANAGER);
    }
}
