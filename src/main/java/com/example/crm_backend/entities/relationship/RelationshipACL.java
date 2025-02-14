package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class RelationshipACL {
    private Relationship relationship;

    public RelationshipACL(Relationship relationship) {
        this.relationship = relationship;
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
