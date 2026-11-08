package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProjectService projectService;

    private Team team;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        team = createTeam("Product");
        user = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        project = createProject(team, "Website redesign");
    }

    @Test
    void findByIdReturnsProject() {
        assertThat(projectService.findById(project.getId()).name()).isEqualTo("Website redesign");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> projectService.findById(9999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void findUsersReturnsProjectMembersOrderedById() {
        User second = createUser("Jonas", "Weber", "jonas@example.com", UserRole.USER, team, false);
        project.setUsers(new ArrayList<>(List.of(second, user)));
        projectRepository.save(project);

        List<UserDTO> result = projectService.findUsers(project.getId());

        assertThat(result).extracting(UserDTO::id).containsExactly(user.getId(), second.getId());
    }

    @Test
    void findUsersThrowsWhenProjectMissing() {
        assertThatThrownBy(() -> projectService.findUsers(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findTasksReturnsProjectTasksOrderedById() {
        Task first = createTask(user, project, "First");
        Task second = createTask(user, project, "Second");

        List<TaskDTO> result = projectService.findTasks(project.getId());

        assertThat(result).extracting(TaskDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findTasksThrowsWhenProjectMissing() {
        assertThatThrownBy(() -> projectService.findTasks(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateChangesProjectName() {
        ProjectDTO result = projectService.update(project.getId(), new ProjectRequest("New name"));

        assertThat(result.name()).isEqualTo("New name");
    }

    @Test
    void updateThrowsWhenProjectMissing() {
        assertThatThrownBy(() -> projectService.update(9999L, new ProjectRequest("Missing")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveTaskCreatesTodoTaskOwnedByCaller() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(2).withNano(0);

        TaskDTO result = projectService.saveTask(project.getId(), user.getId(),
                new CreateTaskRequest("New task", "Description", TaskPriority.HIGH, dueDate));

        assertThat(result.title()).isEqualTo("New task");
        assertThat(result.description()).isEqualTo("Description");
        assertThat(result.priority()).isEqualTo(TaskPriority.HIGH.name());
        assertThat(result.status()).isEqualTo(TaskStatus.TODO.name());
        assertThat(result.dueDate()).isEqualTo(dueDate);
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.createdBy()).isEqualTo(user.getId());
        assertThat(result.projectId()).isEqualTo(project.getId());
        assertThat(result.assignees()).isEmpty();
    }

    @Test
    void saveTaskThrowsWhenProjectOrUserMissing() {
        CreateTaskRequest request = new CreateTaskRequest(
                "New task", null, TaskPriority.LOW, null);

        assertThatThrownBy(() -> projectService.saveTask(9999L, user.getId(), request))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> projectService.saveTask(project.getId(), 9999L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteRemovesProject() {
        projectService.delete(project.getId());

        assertThat(projectRepository.existsById(project.getId())).isFalse();
    }
}
