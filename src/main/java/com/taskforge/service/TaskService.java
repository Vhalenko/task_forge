package com.taskforge.service;

import com.taskforge.mapper.TaskMapper;
import com.taskforge.model.dto.CreateTaskRequestDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UpdateTaskRequestDTO;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    public List<TaskDTO> getAllTasks() {
        return this.taskRepository.findAll()
                .stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public TaskDTO getTask(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task with id: " + id));
        return taskMapper.toTaskDto(task);
    }

    public TaskDTO createTask(CreateTaskRequestDTO dto) {
        User creator = userService.getById(dto.getCreatedBy());
        User assignee = userService.getById(dto.getAssignedTo()).orElseThrow(() ->
                new EntityNotFoundException("no assignee with id: " + dto.getAssignedTo()));
        Task task = Task.builder()
                .title(dto.getTitle())
                .priority(TaskPriority.valueOf(dto.getPriority()))
                .status(TaskStatus.valueOf(dto.getStatus()))
                .createdBy(creator)
                .assignedTo(assignee)
                .build();

        return taskMapper.toTaskDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long id, UpdateTaskRequestDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("no task with id: " + id));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));

        if (task.getAssignedTo().getId() != (dto.getAssignedTo())) {
            task.setAssignedTo(userService.findById(dto.getAssignedTo())
                    .orElseThrow(() ->
                            new EntityNotFoundException("no assignee with id: " + dto.getAssignedTo())));
        }

        return taskMapper.toTaskDto(task);
    }

    public void deleteTask(Long id) {
        taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task found with id: " + id));

        taskRepository.deleteById(id);
    }
}
