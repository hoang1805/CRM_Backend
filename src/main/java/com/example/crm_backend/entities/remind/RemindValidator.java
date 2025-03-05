package com.example.crm_backend.entities.remind;

import com.example.crm_backend.services.RemindService;
import com.example.crm_backend.utils.Timer;

public class RemindValidator {
    private final Remind remind;

    private final RemindService remind_service;

    public RemindValidator(Remind remind, RemindService remind_service) {
        this.remind = remind;
        this.remind_service = remind_service;
    }

    public RemindValidator validRemindTime() {
        if (remind.getRemindTime() == null || (remind.getRemindTime() < Timer.now() && !remind.isReminded())) {
            throw new IllegalStateException("Invalid remind time");
        }

        return this;
    }

    public RemindValidator validSystemId() {
        if (remind.getSystemId() == null) {
            throw new IllegalStateException("Invalid system id");
        }

        return this;
    }

    public void validate() {
        validRemindTime().validSystemId();
    }
}
