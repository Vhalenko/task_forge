package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest extends IntegrationTestSupport {

    @Autowired
    private TaskService taskService;

    private User creator;
    private User otherUser;
    private Project project;

    @BeforeEach
    void setUp() {
        Team team = createTeam("Product");
        creator = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        otherUser = createUser("Jonas", "Weber", "jonas@example.com", UserRole.USER, team, false);
        project = createProject(team, "Website redesign");
    }

    @Test
    void findByIdReturnsMappedTask() {
        Task task = createTask(creator, project, "Write tests");

        TaskDTO result = taskService.findById(task.getId());

        assertThat(result.id()).isEqualTo(task.getId());
        assertThat(result.title()).isEqualTo("Write tests");
        assertThat(result.status()).isEqualTo(TaskStatus.TODO.name());
        assertThat(result.priority()).isEqualTo(TaskPriority.MEDIUM.name());
        assertThat(result.projectId()).isEqualTo(project.getId());
        assertThat(result.projectName()).isEqualTo("Website redesign");
        assertThat(result.createdBy()).isEqualTo(creator.getId());
    }

    @Test
    void findByIdThrowsWhenTaskDoesNotExist() {
        assertThatThrownBy(() -> taskService.findById(9999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void findAllReturnsTasksOrderedById() {
        Task first = createTask(creator, project, "First");
        Task second = createTask(creator, project, "Second");

        List<TaskDTO> result = taskService.findAll();

        assertThat(result).extracting(TaskDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void updateChangesFieldsWhenCalledByCreator() {
        Task task = createTask(creator, project, "Old title");
        LocalDateTime dueDate = LocalDateTime.now().plusDays(3).withNano(0);

        TaskDTO result = taskService.update(task.getId(), new UpdateTaskRequest(
                "New title", "New description", TaskPriority.HIGH, TaskStatus.IN_PROGRESS, dueDate),
                creator.getId());

        assertThat(result.title()).isEqualTo("New title");
        assertThat(result.description()).isEqualTo("New description");
        assertThat(result.priority()).isEqualTo(TaskPriority.HIGH.name());
        assertThat(result.status()).isEqualTo(TaskStatus.IN_PROGRESS.name());
        assertThat(result.dueDate()).isEqualTo(dueDate);
        assertThat(result.completedAt()).isNull();
    }

    @Test
    void updateSetsCompletedAtWhenStatusBecomesDone() {
        Task task = createTask(creator, project, "Finish me");

        TaskDTO result = taskService.update(task.getId(), new UpdateTaskRequest(
                "Finish me", "Done", TaskPriority.MEDIUM, TaskStatus.DONE, null),
                creator.getId());

        assertThat(result.status()).isEqualTo(TaskStatus.DONE.name());
        assertThat(result.completedAt()).isNotNull();
    }

    @Test
    void updateRejectsNonCreatorAndLeavesTaskUntouched() {
        Task task = createTask(creator, project, "Original");

        assertThatThrownBy(() -> taskService.update(task.getId(), new UpdateTaskRequest(
                "Hijacked", null, TaskPriority.LOW, TaskStatus.DONE, null),
                otherUser.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("permission");

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getTitle()).isEqualTo("Original");
    }

    @Test
    void updateThrowsWhenTaskDoesNotExist() {
        assertThatThrownBy(() -> taskService.update(9999L, new UpdateTaskRequest(
                "Missing", null, TaskPriority.LOW, TaskStatus.TODO, null), creator.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteRemovesTaskForCreator() {
        Task task = createTask(creator, project, "Delete me");

        taskService.delete(task.getId(), creator.getId());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }

    @Test
    void deleteRejectsNonCreator() {
        Task task = createTask(creator, project, "Protected");

        assertThatThrownBy(() -> taskService.delete(task.getId(), otherUser.getId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(taskRepository.existsById(task.getId())).isTrue();
    }

    @Test
    void assignAddsUserToTask() {
        Task task = createTask(creator, project, "Assign me");

        taskService.assign(task.getId(), otherUser.getId());

        Task reloaded = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(reloaded.getAssignees()).extracting(User::getId).contains(otherUser.getId());
    }

    @Test
    void assignRejectsDuplicateAssignee() {
        Task task = createTask(creator, project, "Assign me");
        taskService.assign(task.getId(), otherUser.getId());

        assertThatThrownBy(() -> taskService.assign(task.getId(), otherUser.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(
                        ((ResponseStatusException) exception).getStatusCode().value()).isEqualTo(409));
    }

    @Test
    void assignThrowsForMissingTaskOrUser() {
        Task task = createTask(creator, project, "Assign me");

        assertThatThrownBy(() -> taskService.assign(9999L, otherUser.getId()))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> taskService.assign(task.getId(), 9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findCommentsReturnsNewestFirst() {
        Task task = createTask(creator, project, "Comments");
        Comment older = createComment(task, creator, "Old", LocalDateTime.now().minusHours(2));
        Comment newer = createComment(task, otherUser, "New", LocalDateTime.now().minusHours(1));

        List<CommentDTO> result = taskService.findComments(task.getId());

        assertThat(result).extracting(CommentDTO::id).containsExactly(newer.getId(), older.getId());
    }

    @Test
    void findCommentsThrowsWhenTaskDoesNotExist() {
        assertThatThrownBy(() -> taskService.findComments(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveCommentCreatesCommentForCaller() {
        Task task = createTask(creator, project, "Comments");

        CommentDTO result = taskService.saveComment(task.getId(), otherUser.getId(),
                new CommentRequest("Looks good"));

        assertThat(result.text()).isEqualTo("Looks good");
        assertThat(result.createdById()).isEqualTo(otherUser.getId());
        assertThat(result.createdByName()).isEqualTo(otherUser.getUsername());
        assertThat(result.createdAt()).isNotNull();
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    void saveCommentThrowsForMissingTaskOrUser() {
        Task task = createTask(creator, project, "Comments");

        assertThatThrownBy(() -> taskService.saveComment(9999L, creator.getId(),
                new CommentRequest("Text")))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> taskService.saveComment(task.getId(), 9999L,
                new CommentRequest("Text")))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
