package com.example.crm_backend.entities.source;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class SourceACL {
    private final Source source;

    public SourceACL(Source source) {
        this.source = source;
    }

    public boolean canView(User user){
        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(source.getSystemId(), user.getSystemId());
    }

    public boolean canEdit(User user){
        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(source.getSystemId(), user.getSystemId());
    }

    public boolean canDelete(User user){
        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(source.getSystemId(), user.getSystemId());
    }
}
