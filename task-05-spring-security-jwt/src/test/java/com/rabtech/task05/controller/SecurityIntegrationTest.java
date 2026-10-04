package com.rabtech.task05.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabtech.task05.dto.LoginRequest;
import com.rabtech.task05.dto.RegisterRequest;
import com.rabtech.task05.entity.Role;
import com.rabtech.task05.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register USER successfully and hash password with BCrypt")
    void testRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest("user1", "password123", Role.USER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.role").value("USER"));

        var userOpt = userRepository.findByUsername("user1");
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getPassword()).startsWith("$2a$");
    }

    @Test
    @DisplayName("Should reject registration with duplicate username")
    void testRegisterDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest("duplicateUser", "password123", Role.USER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already taken: duplicateUser"));
    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void testLoginSuccess() throws Exception {
        RegisterRequest reg = new RegisterRequest("loginUser", "password123", Role.USER);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("loginUser", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("loginUser"));
    }

    @Test
    @DisplayName("Should fail login with incorrect credentials")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequest login = new LoginRequest("nonExistentUser", "wrongPass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoint should reject unauthenticated request with 401 JSON")
    void testProtectedEndpointUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/secure/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    @DisplayName("USER should access /api/secure/user with valid JWT")
    void testUserAccessProtectedUserEndpoint() throws Exception {
        RegisterRequest reg = new RegisterRequest("normalUser", "password123", Role.USER);
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/secure/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You are authenticated"))
                .andExpect(jsonPath("$.username").value("normalUser"));
    }

    @Test
    @DisplayName("USER should be denied access to /api/admin/dashboard with 403 Forbidden")
    void testUserDeniedAdminEndpoint() throws Exception {
        RegisterRequest reg = new RegisterRequest("regularUser", "password123", Role.USER);
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied: insufficient permissions"));
    }

    @Test
    @DisplayName("ADMIN should access /api/admin/dashboard with valid JWT")
    void testAdminAccessAdminEndpoint() throws Exception {
        RegisterRequest reg = new RegisterRequest("adminUser", "password123", Role.ADMIN);
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome to Admin Dashboard"))
                .andExpect(jsonPath("$.username").value("adminUser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}

