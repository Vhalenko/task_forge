package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.dto.request.TeamRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamService teamService;

    @Test
    void findAllReturnsTeamsOrderedById() {
        Team first = createTeam("First");
        Team second = createTeam("Second");

        List<TeamDTO> result = teamService.findAll();

        assertThat(result).extracting(TeamDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findByIdReturnsTeam() {
        Team team = createTeam("Product");

        assertThat(teamService.findById(team.getId()).name()).isEqualTo("Product");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> teamService.findById(9999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void saveCreatesTeam() {
        TeamDTO result = teamService.save(new TeamRequest("Platform"));

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Platform");
        assertThat(teamRepository.count()).isEqualTo(1);
    }

    @Test
    void deleteRemovesTeam() {
        Team team = createTeam("Temporary");

        teamService.delete(team.getId());

        assertThat(teamRepository.existsById(team.getId())).isFalse();
    }

    @Test
    void findUsersReturnsTeamMembersOrderedById() {
        Team team = createTeam("Product");
        Team otherTeam = createTeam("Other");
        User first = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
        User second = createUser("Jonas", "Weber", "jonas@example.com", UserRole.USER, team, false);
        createUser("Elsewhere", "Person", "else@example.com", UserRole.USER, otherTeam, false);

        List<UserDTO> result = teamService.findUsers(team.getId());

        assertThat(result).extracting(UserDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findUsersThrowsWhenTeamMissing() {
        assertThatThrownBy(() -> teamService.findUsers(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findProjectsReturnsTeamProjectsOrderedById() {
        Team team = createTeam("Product");
        Team otherTeam = createTeam("Other");
        Project first = createProject(team, "First");
        Project second = createProject(team, "Second");
        createProject(otherTeam, "Elsewhere");

        List<ProjectDTO> result = teamService.findProjects(team.getId());

        assertThat(result).extracting(ProjectDTO::id).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findProjectsThrowsWhenTeamMissing() {
        assertThatThrownBy(() -> teamService.findProjects(9999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveProjectCreatesProjectForTeam() {
        Team team = createTeam("Product");

        ProjectDTO result = teamService.saveProject(team.getId(), new ProjectRequest("Mobile app"));

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Mobile app");
        assertThat(projectRepository.findById(result.id()).orElseThrow().getTeam().getId())
                .isEqualTo(team.getId());
    }

    @Test
    void saveProjectThrowsWhenTeamMissing() {
        assertThatThrownBy(() -> teamService.saveProject(9999L, new ProjectRequest("Missing")))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
