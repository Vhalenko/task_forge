package com.taskforge.controller;

import com.taskforge.model.dto.CreateTaskRequestDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UpdateTaskRequestDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.service.TaskService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(_taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_taskService.updateTask(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        _taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
