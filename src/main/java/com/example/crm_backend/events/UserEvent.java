package com.example.crm_backend.events;

import com.example.crm_backend.enums.Event;
import com.example.crm_backend.services.UserService;

public class UserEvent extends AppEvent{
    private final UserService user_service;

    private UserEvent(Object source, Event event, UserService user_service) {
        super(source, event);
        this.user_service = user_service;
    }

    public static UserEvent created(Object source, UserService user_service) {
        return new UserEvent(source, Event.CREATED, user_service);
    }

    public static UserEvent deleted(Object source, UserService user_service) {
        return new UserEvent(source, Event.DELETED, user_service);
    }

    public static UserEvent edited(Object source, UserService user_service) {
        return new UserEvent(source, Event.EDITED, user_service);
    }

    @Override
    public UserService getService() {
        return user_service;
    }
}
