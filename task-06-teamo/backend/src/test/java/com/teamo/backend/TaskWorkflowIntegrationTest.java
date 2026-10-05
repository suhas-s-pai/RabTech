package com.teamo.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamo.backend.dto.AuthRequest;
import com.teamo.backend.dto.RegisterRequest;
import com.teamo.backend.dto.TaskCreateRequest;
import com.teamo.backend.dto.TaskReviewRequest;
import com.teamo.backend.dto.TaskSubmitRequest;
import com.teamo.backend.entity.Role;
import com.teamo.backend.repository.TaskRepository;
import com.teamo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private String managerToken;
    private String employeeToken;
    private Long managerId;
    private Long employeeId;

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Register Krishna (Manager)
        RegisterRequest managerReg = new RegisterRequest("Krishna", "krishna@rabtech.com", "password123", Role.MANAGER);
        MvcResult mResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerReg)))
                .andExpect(status().isCreated())
                .andReturn();
        managerToken = objectMapper.readTree(mResult.getResponse().getContentAsString()).get("token").asText();
        managerId = objectMapper.readTree(mResult.getResponse().getContentAsString()).get("user").get("id").asLong();

        // 2. Register Suhas (Employee)
        RegisterRequest empReg = new RegisterRequest("Suhas", "suhas@rabtech.com", "password123", Role.EMPLOYEE);
        MvcResult eResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empReg)))
                .andExpect(status().isCreated())
                .andReturn();
        employeeToken = objectMapper.readTree(eResult.getResponse().getContentAsString()).get("token").asText();
        employeeId = objectMapper.readTree(eResult.getResponse().getContentAsString()).get("user").get("id").asLong();
    }

    @Test
    @DisplayName("Complete Task Workflow: ASSIGNED -> IN_PROGRESS -> SUBMITTED -> APPROVED")
    void testCompleteTaskWorkflow() throws Exception {
        // Step 1: Krishna (Manager) creates and assigns a task to Suhas (Employee)
        TaskCreateRequest createReq = new TaskCreateRequest(
                "Build Authentication Module",
                "Implement JWT token authentication with Spring Security 6",
                LocalDateTime.now().plusDays(5),
                employeeId
        );

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedTo.name").value("Suhas"))
                .andExpect(jsonPath("$.createdBy.name").value("Krishna"))
                .andReturn();

        Long taskId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Step 2: Suhas (Employee) views assigned tasks
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].title").value("Build Authentication Module"));

        // Step 3: Suhas (Employee) starts the task
        mockMvc.perform(put("/api/tasks/" + taskId + "/start")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Step 4: Suhas (Employee) submits completed task with GitHub URL & comment
        TaskSubmitRequest submitReq = new TaskSubmitRequest(
                "https://github.com/suhas-s-pai/RabTech/tree/main/task-06-teamo",
                "Completed Spring Security 6 JWT integration and unit tests."
        );

        mockMvc.perform(put("/api/tasks/" + taskId + "/submit")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.githubUrl").isNotEmpty())
                .andExpect(jsonPath("$.submissionComment").isNotEmpty());

        // Step 5: Krishna (Manager) reviews and approves the task
        TaskReviewRequest reviewReq = new TaskReviewRequest("Excellent work! Approved.");

        mockMvc.perform(put("/api/tasks/" + taskId + "/approve")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.managerFeedback").value("Excellent work! Approved."));
    }

    @Test
    @DisplayName("Security & Role Authorization Enforcement")
    void testSecurityRoleAuthorization() throws Exception {
        // Employee attempting to create a task should be rejected with 403 Forbidden
        TaskCreateRequest createReq = new TaskCreateRequest("Unauthorized Task", "Desc", LocalDateTime.now().plusDays(2), employeeId);

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));

        // Unauthenticated request should be rejected with 401 Unauthorized
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }
}

