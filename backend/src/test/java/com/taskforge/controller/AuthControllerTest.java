package com.taskforge.controller;

import com.taskforge.model.dto.request.LoginRequest;
import com.taskforge.model.dto.request.RegisterRequest;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTestSupport {

    @Test
    void registerCreatesAccountAndReturnsCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Maya", "Lindqvist", "maya", "Maya@Example.COM", "secret123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Maya"))
                .andExpect(jsonPath("$.lastName").value("Lindqvist"))
                .andExpect(jsonPath("$.username").value("maya"))
                .andExpect(jsonPath("$.email").value("maya@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.isBlocked").value(false));

        assertThat(userRepository.findByEmailIgnoreCase("maya@example.com")).isPresent();
    }

    @Test
    void registerRejectsDuplicateEmail() throws Exception {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest(
                                "Other", "Person", "other", "MAYA@example.com", "secret123"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Email is in use"));
    }

    @Test
    void registerRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest("", "", "", "", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void loginReturnsTokenForValidCredentials() throws Exception {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("maya@example.com", DEFAULT_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("maya@example.com"));
    }

    @Test
    void loginReturnsUnauthorizedForWrongPassword() throws Exception {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("maya@example.com", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void loginReturnsForbiddenForBlockedAccount() throws Exception {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, true);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("maya@example.com", DEFAULT_PASSWORD))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void loginRejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }
}
