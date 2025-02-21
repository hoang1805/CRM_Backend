package com.example.crm_backend.entities.account.product;

import com.example.crm_backend.services.account.AccountProductService;
import com.example.crm_backend.utils.Validator;

public class AccountProductValidator extends Validator {
    private final AccountProduct account_product;

    private final AccountProductService account_product_service;

    public AccountProductValidator(AccountProduct account_product, AccountProductService accountProductService) {
        this.account_product = account_product;
        account_product_service = accountProductService;
    }

    public AccountProductValidator validName() {
        if (account_product.getName() == null || account_product.getName().isEmpty()) {
            throw new IllegalStateException("Name is empty. Please try again");
        }

        return this;
    }

    public AccountProductValidator validCategory() {
        if (account_product.getCategory() == null || account_product.getCategory().isEmpty()) {
            throw new IllegalStateException("Category is empty. Please try again");
        }

        return this;
    }

    public AccountProductValidator validPrice() {
        if (account_product.getPrice() == null || account_product.getPrice() <= 0) {
            throw new IllegalStateException("Invalid price. Please try again");
        }

        return this;
    }

    public AccountProductValidator validTax() {
        if (account_product.getTax() != null && account_product.getTax() < 0) {
            throw new IllegalStateException("Invalid tax. Please try again");
        }

        return this;
    }

    public AccountProductValidator validDiscount() {
        if (account_product.getDiscount() != null && account_product.getDiscount() < 0) {
            throw new IllegalStateException("Invalid discount. Please try again");
        }

        return this;
    }

    public AccountProductValidator validAccount() {
        if (account_product.getAccountId() == null) {
            throw new IllegalStateException("Invalid account. Please try again");
        }

        if (!account_product_service.isExistAccount(account_product.getAccountId())) {
            throw new IllegalStateException("Invalid account. Please try again");
        }

        return this;
    }

    public AccountProductValidator validQuantity() {
        if (account_product.getQuantity() == null || account_product.getQuantity() < 0) {
            throw new IllegalStateException("Invalid quantity. Please try again");
        }

        return this;
    }

    public void validate() {
        validName().validPrice().validTax().validCategory().validDiscount().validAccount().validQuantity();
    }
}
