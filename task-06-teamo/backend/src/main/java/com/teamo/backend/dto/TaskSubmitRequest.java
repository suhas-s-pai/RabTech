package com.teamo.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskSubmitRequest {

    @NotBlank(message = "GitHub Repository URL is required")
    private String githubUrl;

    private String submissionComment;

    public TaskSubmitRequest() {
    }

    public TaskSubmitRequest(String githubUrl, String submissionComment) {
        this.githubUrl = githubUrl;
        this.submissionComment = submissionComment;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getSubmissionComment() {
        return submissionComment;
    }

    public void setSubmissionComment(String submissionComment) {
        this.submissionComment = submissionComment;
    }
}

