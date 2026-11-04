package com.taskforge.controller;

import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.entity.Comment;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends IntegrationTestSupport {

    private User author;
    private User otherUser;
    private Comment comment;

    @BeforeEach
    void setUp() {
        Team team = createTeam("Product");
        author = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        otherUser = createUser("Jonas", "Weber", "jonas@example.com", UserRole.USER, team, false);
        Project project = createProject(team, "Website redesign");
        Task task = createTask(author, project, "Comments");
        comment = createComment(task, author, "Original", LocalDateTime.now());
    }

    @Test
    void updateChangesOwnComment() throws Exception {
        mockMvc.perform(put("/api/v1/comments/{id}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CommentRequest("Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated"));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getText())
                .isEqualTo("Updated");
    }

    @Test
    void updateRejectsSomeoneElsesComment() throws Exception {
        mockMvc.perform(put("/api/v1/comments/{id}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CommentRequest("Hijacked"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateReturnsNotFoundForMissingComment() throws Exception {
        mockMvc.perform(put("/api/v1/comments/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CommentRequest("Text"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRemovesOwnComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{id}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(author)))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.existsById(comment.getId())).isFalse();
    }

    @Test
    void deleteRejectsSomeoneElsesComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{id}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherUser)))
                .andExpect(status().isForbidden());

        assertThat(commentRepository.existsById(comment.getId())).isTrue();
    }

    @Test
    void deleteReturnsNotFoundForMissingComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(author)))
                .andExpect(status().isNotFound());
    }
}
