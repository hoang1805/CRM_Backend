package com.example.crm_backend.entities.user;

import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.Validator;

public class UserValidator extends Validator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private User user;
    private UserService user_service;

    public UserValidator(User user, UserService user_service) {
        this.user = user;
        this.user_service = user_service;
    }

    public UserValidator validEmail() {
        if (!UserValidator.isValidStr(user.getEmail(), EMAIL_REGEX)) {
            throw new IllegalStateException("Invalid email");
        }

        return this;
    }


    public UserValidator validPassword() {
        if (!UserValidator.isValidStr(user.getPassword(), PASSWORD_REGEX)) {
            throw new IllegalStateException("Invalid password");
        }

        return this;
    }

    public void validate(){
        validEmail().validPassword();

        if (user.getId() == null && user_service.isExisted(user.getUsername(), user.getEmail())) {
            throw new IllegalStateException("User has already existed");
        }

        if (user.getName() == null) {
            throw new IllegalStateException("Fullname must not be empty");
        }

    }
}
