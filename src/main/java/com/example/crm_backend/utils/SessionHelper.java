package com.example.crm_backend.utils;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionHelper {
    public static User getSessionUser(HttpServletRequest request, UserService user_service){
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Long user_id = (Long) session.getAttribute("user_id");
        return user_service.getUser(user_id);
    }
}
