package com.teamo.backend.dto;

public class TaskReviewRequest {

    private String feedback;

    public TaskReviewRequest() {
    }

    public TaskReviewRequest(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

