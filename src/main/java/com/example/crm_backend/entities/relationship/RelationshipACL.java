package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class RelationshipACL {
    private final Relationship relationship;

    public RelationshipACL(Relationship relationship) {
        this.relationship = relationship;
    }

    public boolean canView(User user){
        if (user.getRole() == Role.SUPER_ADMIN) {
            return true;
        }

        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(relationship.getSystemId(), user.getSystemId());
    }

    public boolean canEdit(User user){
        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(relationship.getSystemId(), user.getSystemId());
    }

    public boolean canDelete(User user){
        Role user_role = user.getRole();
        boolean ok = user_role == Role.ADMIN || user_role == Role.MANAGER;

        return ok && Objects.equals(relationship.getSystemId(), user.getSystemId());
    }
}
