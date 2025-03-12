package com.example.crm_backend.events;

import com.example.crm_backend.enums.Event;
import com.example.crm_backend.services.account.AccountService;

public class AccountEvent extends AppEvent {
    private final AccountService account_service;
    private AccountEvent(Object source, Event event, AccountService account_service) {
        super(source, event);
        this.account_service = account_service;
    }

    public static AccountEvent created(Object source, AccountService account_service) {
        return new AccountEvent(source, Event.CREATED, account_service);
    }

    public static AccountEvent deleted(Object source, AccountService account_service) {
        return new AccountEvent(source, Event.DELETED, account_service);
    }

    public static AccountEvent imported(AccountService account_service) {
        return new AccountEvent(new Object(), Event.IMPORTED, account_service);
    }

    public static AccountEvent edited(Object source, AccountService account_service) {
        return new AccountEvent(source, Event.EDITED, account_service);
    }

    @Override
    public AccountService getService() {
        return account_service;
    }
}
