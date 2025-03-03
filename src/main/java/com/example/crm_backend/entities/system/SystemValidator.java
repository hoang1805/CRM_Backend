package com.example.crm_backend.entities.system;

import com.example.crm_backend.services.SystemService;
import com.example.crm_backend.utils.Validator;

public class SystemValidator extends Validator {
    private final System system;

    private final SystemService system_service;

    public SystemValidator(System system, SystemService system_service) {
        this.system = system;
        this.system_service = system_service;
    }

    public SystemValidator validName() {
        if (system.getName() == null) {
            throw new IllegalStateException("Invalid system name");
        }

        return this;
    }

    public SystemValidator validMaxUser() {
        if (system.getMaxUser() == null || system.getMaxUser() <= 0) {
            throw new IllegalStateException("Invalid system max user");
        }

        return this;
    }

    public void validate() {
        validName().validMaxUser();
    }
}
