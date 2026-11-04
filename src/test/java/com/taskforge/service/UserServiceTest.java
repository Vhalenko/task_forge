package com.taskforge.service;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.dto.request.UpdateUserStatusRequest;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

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
    void findByIdReturnsUser() {
        UserDTO result = userService.findById(user.getId());

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo("maya@example.com");
        assertThat(result.teamId()).isEqualTo(team.getId());
        assertThat(result.teamName()).isEqualTo("Product");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> userService.findById(9999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void findTasksReturnsAssignedTasksOrderedById() {
        Task first = createTask(user, project, "First");
        Task second = createTask(user, project, "Second");
        Task unassigned = createTask(user, project, "Unassigned");
        second.getAssignees().add(user);
        first.getAssignees().add(user);
        taskRepository.saveAll(List.of(second, first, unassigned));

        List<TaskDTO> result = userService.findTasks(user.getId());

        assertThat(result).extracting(TaskDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findTasksThrowsWhenUserMissing() {
        assertThatThrownBy(() -> userService.findTasks(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateRoleChangesRole() {
        userService.updateRole(user.getId(), new UpdateRoleRequest(UserRole.ADMIN));

        assertThat(userRepository.findById(user.getId()).orElseThrow().getRole())
                .isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateRoleThrowsWhenUserMissing() {
        assertThatThrownBy(() ->
                userService.updateRole(9999L, new UpdateRoleRequest(UserRole.ADMIN)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateStatusBlocksAndUnblocksUser() {
        userService.updateStatus(user.getId(), new UpdateUserStatusRequest(true));

        assertThat(userRepository.findById(user.getId()).orElseThrow().isBlocked()).isTrue();
    }

    @Test
    void updateStatusThrowsWhenUserMissing() {
        assertThatThrownBy(() ->
                userService.updateStatus(9999L, new UpdateUserStatusRequest(true)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateChangesProfileAndLowercasesEmail() {
        UserDTO result = userService.update(
                new UpdateUserRequest("New", "Name", "", "NEW@EXAMPLE.COM"), user.getId());

        assertThat(result.firstName()).isEqualTo("New");
        assertThat(result.lastName()).isEqualTo("Name");
        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.username()).isEqualTo("maya.lindqvist");
    }

    @Test
    void updateThrowsWhenCallerMissing() {
        assertThatThrownBy(() -> userService.update(
                new UpdateUserRequest("New", "Name", "new", "new@example.com"), 9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateMeChangesAllProfileFields() {
        UserDTO result = userService.updateMe(
                new UpdateUserRequest("New", "Name", "new.username", "new@example.com"),
                user.getId());

        assertThat(result.firstName()).isEqualTo("New");
        assertThat(result.lastName()).isEqualTo("Name");
        assertThat(result.username()).isEqualTo("new.username");
        assertThat(result.email()).isEqualTo("new@example.com");
    }

    @Test
    void updateMeThrowsWhenCallerMissing() {
        assertThatThrownBy(() -> userService.updateMe(
                new UpdateUserRequest("New", "Name", "new", "new@example.com"), 9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateTeamAssignsUserToTeam() {
        Team newTeam = createTeam("Platform");

        userService.updateTeam(user.getId(), newTeam.getId());

        assertThat(userRepository.findById(user.getId()).orElseThrow().getTeam().getId())
                .isEqualTo(newTeam.getId());
    }

    @Test
    void updateTeamThrowsWhenUserOrTeamMissing() {
        assertThatThrownBy(() -> userService.updateTeam(9999L, team.getId()))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> userService.updateTeam(user.getId(), 9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
