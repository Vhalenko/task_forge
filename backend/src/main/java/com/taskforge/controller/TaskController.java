package com.taskforge.controller;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequest;
import com.taskforge.security.CurrentUserId;
import com.taskforge.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody UpdateTaskRequest request,
                                          @CurrentUserId Long callerId) {
        return ResponseEntity.ok(taskService.update(id, request, callerId));
    }

    @PatchMapping("/{taskId}/assignee/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assign(
            @PathVariable(name = "taskId") Long taskId,
            @PathVariable(name = "userId")Long userId) {
        taskService.assign(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/assignee/me")
    public ResponseEntity<Void> assignMe(
            @PathVariable(name = "taskId") Long taskId,
            @CurrentUserId Long callerId) {
        taskService.assign(taskId, callerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUserId Long callerId) {
        taskService.delete(id, callerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> findComments(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> saveComment(
            @PathVariable Long id,
            @CurrentUserId Long callerId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.saveComment(id, callerId, request));
    }
}
