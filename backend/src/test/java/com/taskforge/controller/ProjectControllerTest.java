package com.taskforge.controller;

import com.taskforge.model.dto.request.CreateTaskRequest;
import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends IntegrationTestSupport {

    private Project project;
    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        Team team = createTeam("Product");
        admin = createUser("Admin", "User", "admin@example.com", UserRole.ADMIN, team, false);
        user = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        project = createProject(team, "Website redesign");
    }

    @Test
    void findByIdReturnsProject() throws Exception {
        mockMvc.perform(get("/api/v1/projects/{id}", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId()))
                .andExpect(jsonPath("$.name").value("Website redesign"));
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/projects/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findTasksReturnsProjectTasksOrderedById() throws Exception {
        Task first = createTask(user, project, "First");
        Task second = createTask(user, project, "Second");

        mockMvc.perform(get("/api/v1/projects/{id}/tasks", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(first.getId()))
                .andExpect(jsonPath("$[1].id").value(second.getId()));
    }

    @Test
    void saveTaskCreatesTaskForCaller() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "New task", "Description", TaskPriority.HIGH, null);

        mockMvc.perform(post("/api/v1/projects/{id}/tasks", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New task"))
                .andExpect(jsonPath("$.status").value(TaskStatus.TODO.name()))
                .andExpect(jsonPath("$.priority").value(TaskPriority.HIGH.name()))
                .andExpect(jsonPath("$.createdBy").value(user.getId()));

        assertThat(taskRepository.findAll()).hasSize(1);
    }

    @Test
    void saveTaskRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/projects/{id}/tasks", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateTaskRequest("", null, null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateRequiresAdmin() throws Exception {
        ProjectRequest request = new ProjectRequest("Renamed project");

        mockMvc.perform(put("/api/v1/projects/{id}", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/projects/{id}", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Renamed project"));
    }

    @Test
    void deleteRequiresAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/{id}", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/projects/{id}", project.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.existsById(project.getId())).isFalse();
    }
}
