package com.taskforge.controller;

import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationTestSupport {

    private User user;
    private User admin;
    private Task assignedTask;

    @BeforeEach
    void setUp() {
        Team team = createTeam("Product");
        user = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        admin = createUser("Admin", "User", "admin@example.com", UserRole.ADMIN, team, false);
        Project project = createProject(team, "Website redesign");
        assignedTask = createTask(user, project, "My task");
        assignedTask.getAssignees().add(user);
        taskRepository.save(assignedTask);
    }

    @Test
    void findMeReturnsAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("maya@example.com"));
    }

    @Test
    void findMyTasksReturnsAssignedTasks() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/tasks")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(assignedTask.getId()));
    }

    @Test
    void findByIdReturnsUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admin.getId()));
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoleRequiresAdmin() throws Exception {
        UpdateRoleRequest request = new UpdateRoleRequest(UserRole.ADMIN);

        mockMvc.perform(patch("/api/v1/users/{id}/role", user.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId()).orElseThrow().getRole())
                .isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateRoleRejectsNonAdmin() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}/role", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new UpdateRoleRequest(UserRole.USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatusRequiresAdmin() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}/blocked", user.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isBlocked\":true}"))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId()).orElseThrow().isBlocked()).isTrue();
    }

    @Test
    void updateStatusRejectsNonAdmin() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}/blocked", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isBlocked\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateMeChangesProfile() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "New", "Name", "new.username", "new@example.com");

        mockMvc.perform(put("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.username").value("new.username"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void updateMeRejectsInvalidPayload() throws Exception {
        mockMvc.perform(put("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new UpdateUserRequest("", "", "user", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
