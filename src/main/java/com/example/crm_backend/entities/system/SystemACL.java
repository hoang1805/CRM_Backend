package com.example.crm_backend.entities.system;


import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class SystemACL {
    private final System system;

    public SystemACL(System system) {
        this.system = system;
    }

    public boolean canView(User user) {
        Role role = user.getRole();
        return Objects.equals(role, Role.SUPER_ADMIN);
    }

    public boolean canEdit(User user) {
        Role role = user.getRole();
        return Objects.equals(role, Role.SUPER_ADMIN);
    }

    public boolean canDelete(User user) {
        Role role = user.getRole();
        return Objects.equals(role, Role.SUPER_ADMIN);
    }
}
