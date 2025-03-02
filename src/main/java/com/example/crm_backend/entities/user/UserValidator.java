package com.example.crm_backend.entities.user;

import com.example.crm_backend.entities.account.AccountValidator;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.Validator;

import java.util.Objects;

public class UserValidator extends Validator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#?!@$%^&*-_])[A-Za-z\\d#?!@$%^&*-_]{8,}$";
    private static final String PHONE_REGEX = "^(0[3|5|7|8|9])([0-9]{8})$";

    private final User user;
    private final UserService user_service;

    public UserValidator(User user, UserService user_service) {
        this.user = user;
        this.user_service = user_service;
    }

    public UserValidator validEmail() {
        if (Objects.equals(user.getRole(), Role.ADMIN)) {
            return this;
        }

        if (!UserValidator.isValidStr(user.getEmail(), EMAIL_REGEX)) {
            throw new IllegalStateException("Invalid email");
        }

        return this;
    }


    public UserValidator validPassword() {
        if (user.getId() == null) {
            return this;
        }

        if (!UserValidator.isValidStr(user.getPassword(), PASSWORD_REGEX)) {
            throw new IllegalStateException("Invalid password");
        }

        return this;
    }

    public UserValidator validPhone(){
        String phone = user.getPhone();
        if (phone == null || phone.isEmpty()) {
            return this;
        }

        if (!UserValidator.isValidStr(user.getPhone(), PHONE_REGEX)) {
            throw new IllegalStateException("Invalid phone");
        }

        return this;
    }

    public void validate(){
        validEmail().validPhone();

        if (user.getId() == null && user_service.isExisted(user.getUsername(), user.getEmail())) {
            throw new IllegalStateException("User has already existed");
        }

        if (user.getName() == null) {
            throw new IllegalStateException("Fullname must not be empty");
        }

    }
}
