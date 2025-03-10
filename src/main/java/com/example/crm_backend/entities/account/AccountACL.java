package com.example.crm_backend.entities.account;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class AccountACL {
    private final Account account;

    public AccountACL(Account account){
        this.account = account;
    }

    public boolean canView(User user){
        if (user.getRole() == Role.SUPER_ADMIN) {
            return true;
        }

        if (!Objects.equals(account.getSystemId(), user.getSystemId())) {
            return false;
        }

        Role user_role = user.getRole();
        if (Objects.equals(user_role, Role.ADMIN) || Objects.equals(user_role, Role.MANAGER)) {
            return true;
        }

        return true;
//        Long user_id = user.getId();
//        return Objects.equals(user_id, account.getCreatorId()) || Objects.equals(user_id, account.getAssignedUserId());
    }

    public boolean canEdit(User user){
        if (!Objects.equals(account.getSystemId(), user.getSystemId())) {
            return false;
        }

        return Objects.equals(this.account.getCreatorId(), user.getId()) || Objects.equals(user.getRole(), Role.ADMIN);
    }

    public boolean canDelete(User user){
        if (!Objects.equals(account.getSystemId(), user.getSystemId())) {
            return false;
        }

        return Objects.equals(user.getRole(), Role.ADMIN);
    }
}
