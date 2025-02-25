package com.example.crm_backend.entities.account.product;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class AccountProductACL {
    private final AccountProduct account_product;

    public AccountProductACL(AccountProduct account_product) {
        this.account_product = account_product;
    }

    public boolean canView(User user) {
        return true;
    }

    public boolean canEdit(User user) {
        Role role = user.getRole();
        Long id = user.getId();
        return Objects.equals(role, Role.ADMIN) || Objects.equals(id, account_product.getCreatorId());
    }

    public boolean canDelete(User user) {
        Role role = user.getRole();
        Long id = user.getId();
        return Objects.equals(role, Role.ADMIN) || Objects.equals(id, account_product.getCreatorId());
    }
}
