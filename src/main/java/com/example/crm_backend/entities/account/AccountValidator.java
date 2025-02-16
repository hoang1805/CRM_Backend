package com.example.crm_backend.entities.account;

import com.example.crm_backend.services.AccountService;
import com.example.crm_backend.utils.Timer;
import com.example.crm_backend.utils.Validator;

public class AccountValidator extends Validator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^(0[3|5|7|8|9])([0-9]{8})$";

    private Account account;

    private AccountService account_service;

    public AccountValidator(Account account, AccountService account_service) {
        this.account = account;
        this.account_service = account_service;
    }

    public AccountValidator validEmail(){
        if (!AccountValidator.isValidStr(account.getEmail(), EMAIL_REGEX)) {
            throw new IllegalStateException("Invalid email");
        }

        return this;
    }

    public AccountValidator validPhone(){
        String phone = account.getPhone();
        if (phone == null || phone.isEmpty()) {
            return this;
        }

        if (!AccountValidator.isValidStr(account.getPhone(), PHONE_REGEX)) {
            throw new IllegalStateException("Invalid phone");
        }

        return this;
    }

    public void validate(){
        if (account.getName().isEmpty()) {
            throw new IllegalStateException("Account name is empty. Please try again");
        }

        if (account.getCode().isEmpty()) {
            throw new IllegalStateException("Account code is empty. Please try again");
        }

        validEmail().validPhone();

        if (account.getId() == null && account_service.isExist(account)) {
            throw new IllegalStateException("Account code has already existed");
        }

        if (account.getReferrerId() == null) {
            throw new IllegalStateException("Referrer field is empty. Please try again");
        }

        if (!account_service.isValidUser(account.getReferrerId())) {
            throw new IllegalStateException("Invalid referrer. Please try again");
        }

        Long assigned_id = account.getAssignedUserId();
        if (assigned_id != null && !account_service.isValidUser(assigned_id)) {
            throw new IllegalStateException("Invalid assigned user. Please try again");
        }

        if (account.getSourceId() == null) {
            throw new IllegalStateException("Source field is empty. Please try again");
        }

        if (account.getBirthday() > Timer.now()) {
            throw new IllegalStateException("Invalid birthday. Please try again");
        }
    }
}
