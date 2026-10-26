package com.taskforge.controller;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequest;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.update(id, request));
    }

    @PatchMapping("/{taskId}/assignee/{userId}")
    public ResponseEntity<Void> updateAssignee(
            @PathVariable(name = "taskId") Long taskId,
            @PathVariable(name = "userId")Long userId) {
        taskService.updateAssignee(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> findComments(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.findComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> saveComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody CommentRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.saveComment(id, principal.getUser().getId(), request));
    }
}
