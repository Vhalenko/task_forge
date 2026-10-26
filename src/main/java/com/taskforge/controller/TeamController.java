package com.taskforge.controller;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.CreateProjectRequest;
import com.taskforge.model.dto.request.CreateTeamRequest;
import com.taskforge.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findById(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> save(@RequestBody CreateTeamRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teamService.save(request));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserDTO>> findUsers(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findUsers(id));
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> findProjects(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findProjects(id));
    }

    @PostMapping("/{id}/projects")
    public ResponseEntity<ProjectDTO> saveProject(@PathVariable Long id, @RequestBody CreateProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teamService.saveProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
