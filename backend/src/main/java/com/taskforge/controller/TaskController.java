package com.taskforge.controller;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService _taskService;

    public TaskController(TaskService taskService) {
        _taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_taskService.getTask(id));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(_taskService.createTask(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_taskService.updateTask(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        _taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
