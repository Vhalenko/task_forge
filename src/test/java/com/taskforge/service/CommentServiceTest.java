package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

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
        comment = createComment(task, author, "Original text", LocalDateTime.now());
    }

    @Test
    void updateChangesTextForAuthor() {
        CommentDTO result = commentService.update(
                comment.getId(), new CommentRequest("Updated text"), author.getId());

        assertThat(result.text()).isEqualTo("Updated text");
        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getText())
                .isEqualTo("Updated text");
    }

    @Test
    void updateRejectsNonAuthor() {
        assertThatThrownBy(() -> commentService.update(
                comment.getId(), new CommentRequest("Hijacked"), otherUser.getId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getText())
                .isEqualTo("Original text");
    }

    @Test
    void updateThrowsWhenCommentMissing() {
        assertThatThrownBy(() -> commentService.update(
                9999L, new CommentRequest("Text"), author.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    void deleteRemovesCommentForAuthor() {
        commentService.delete(comment.getId(), author.getId());

        assertThat(commentRepository.existsById(comment.getId())).isFalse();
    }

    @Test
    void deleteRejectsNonAuthor() {
        assertThatThrownBy(() -> commentService.delete(comment.getId(), otherUser.getId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(commentRepository.existsById(comment.getId())).isTrue();
    }

    @Test
    void deleteThrowsWhenCommentMissing() {
        assertThatThrownBy(() -> commentService.delete(9999L, author.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
