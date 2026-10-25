package com.taskforge.controller;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CreateCommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequestDTO;
import com.taskforge.model.entity.Comment;
import com.taskforge.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id, @RequestBody UpdateTaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.update(id, dto));
    }

    @PatchMapping("/{task-id}/assignee/{user-id}")
    public ResponseEntity<Void> updateAssignee(
            @PathVariable(name = "task-id") Long taskId,
            @PathVariable(name = "user-id")Long userId) {
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
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.findComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> saveComment(@PathVariable Long id, @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.saveComment(id, request));
    }
}
