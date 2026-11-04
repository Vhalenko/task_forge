package com.taskforge.controller;

import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.dto.request.TeamRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeamControllerTest extends IntegrationTestSupport {

    private Team team;
    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        team = createTeam("Product");
        admin = createUser("Admin", "User", "admin@example.com", UserRole.ADMIN, team, false);
        user = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, team, false);
    }

    @Test
    void findAllReturnsTeamsForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/teams")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Product"));
    }

    @Test
    void findByIdReturnsTeam() throws Exception {
        mockMvc.perform(get("/api/v1/teams/{id}", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(team.getId()))
                .andExpect(jsonPath("$.name").value("Product"));
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/teams/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void createRequiresAdmin() throws Exception {
        TeamRequest request = new TeamRequest("Platform");

        mockMvc.perform(post("/api/v1/teams")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/teams")
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Platform"));

        assertThat(teamRepository.findAll()).extracting(Team::getName).contains("Platform");
    }

    @Test
    void createRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/v1/teams")
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new TeamRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void findUsersRequiresAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/teams/{id}/users", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/teams/{id}/users", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findProjectsReturnsTeamProjects() throws Exception {
        Project project = createProject(team, "Website redesign");

        mockMvc.perform(get("/api/v1/teams/{id}/projects", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(project.getId()));
    }

    @Test
    void saveProjectRequiresAdmin() throws Exception {
        ProjectRequest request = new ProjectRequest("Mobile app");

        mockMvc.perform(post("/api/v1/teams/{id}/projects", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/teams/{id}/projects", team.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mobile app"));
    }

    @Test
    void deleteRequiresAdmin() throws Exception {
        Team emptyTeam = createTeam("Temporary");

        mockMvc.perform(delete("/api/v1/teams/{id}", emptyTeam.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/teams/{id}", emptyTeam.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isNoContent());

        assertThat(teamRepository.existsById(emptyTeam.getId())).isFalse();
    }
}
