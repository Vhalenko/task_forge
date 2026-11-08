package com.taskforge.controller;

import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends IntegrationTestSupport {

    private User creator;
    private User otherUser;
    private User admin;
    private Task task;

    @BeforeEach
    void setUp() {
        Team team = createTeam("Product");
        creator = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        otherUser = createUser("Jonas", "Weber", "jonas@example.com", UserRole.USER, team, false);
        admin = createUser("Admin", "User", "admin@example.com", UserRole.ADMIN, team, false);
        Project project = createProject(team, "Website redesign");
        task = createTask(creator, project, "Write controller tests");
    }

    @Test
    void findByIdReturnsTask() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Write controller tests"))
                .andExpect(jsonPath("$.projectName").value("Website redesign"));
    }

    @Test
    void findByIdReturnsNotFoundForMissingTask() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void updateChangesTaskForCreator() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated", "Updated description", TaskPriority.HIGH, TaskStatus.DONE, null);

        mockMvc.perform(put("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }

    @Test
    void updateRejectsNonCreator() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Hijacked", null, TaskPriority.LOW, TaskStatus.TODO, null);

        mockMvc.perform(put("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void updateRejectsInvalidPayload() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new UpdateTaskRequest("", null, null, null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void deleteRemovesTaskForCreator() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator)))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }

    @Test
    void deleteRejectsNonCreator() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignMeAddsCallerAsAssignee() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/{id}/assignee/me", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser)))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getAssignees())
                .extracting(User::getId)
                .contains(otherUser.getId());
    }

    @Test
    void assignAnotherUserRequiresAdmin() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/{id}/assignee/{userId}", task.getId(), otherUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator)))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/v1/tasks/{id}/assignee/{userId}", task.getId(), otherUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveCommentCreatesComment() throws Exception {
        mockMvc.perform(post("/api/v1/tasks/{id}/comments", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CommentRequest("Looks good"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Looks good"))
                .andExpect(jsonPath("$.createdById").value(otherUser.getId()));
    }

    @Test
    void findCommentsReturnsComments() throws Exception {
        createComment(task, creator, "First comment", java.time.LocalDateTime.now());

        mockMvc.perform(get("/api/v1/tasks/{id}/comments", task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(creator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("First comment"));
    }
}
