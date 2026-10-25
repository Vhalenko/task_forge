package com.taskforge.controller;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.CreateTaskRequest;
import com.taskforge.model.dto.request.UpdateProjectRequest;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.service.ProjectService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectService.findById(id));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserDTO>> findUsers(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectService.findUsers(id));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> findTasks(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectService.findTasks(id));
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskDTO> saveTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal, @RequestBody CreateTaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectService.saveTask(id, principal.getUser().getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody UpdateProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
