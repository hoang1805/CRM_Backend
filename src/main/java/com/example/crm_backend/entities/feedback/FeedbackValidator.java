package com.example.crm_backend.entities.feedback;

import com.example.crm_backend.services.FeedbackService;

public class FeedbackValidator {
    private final Feedback feedback;

    private final FeedbackService feedback_service;

    public FeedbackValidator(Feedback feedback, FeedbackService feedbackService) {
        this.feedback = feedback;
        feedback_service = feedbackService;
    }

    public void validate(){
        if (feedback.getRating() == null) {
            throw new IllegalStateException("Invalid rating");
        }
    }
}
