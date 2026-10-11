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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository _taskRepository;
    private final UserRepository _userRepository;
    private final TaskMapper _taskMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        _taskRepository = taskRepository;
        _userRepository = userRepository;
        _taskMapper = taskMapper;
    }

    public List<TaskDTO> getAllTasks() {
        return _taskRepository.findAll()
                .stream()
                .map(_taskMapper::toTaskDto)
                .toList();
    }

    public TaskDTO getTask(Long id) {
        Task task = _taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task with id: " + id));
        return _taskMapper.toTaskDto(task);
    }

    public TaskDTO createTask(CreateTaskRequestDTO dto) {
        User creator = _userRepository.findById(dto.getCreatedBy()).orElseThrow(() ->
                new EntityNotFoundException("no creator with id: " + dto.getCreatedBy()));
        User assignee = _userRepository.findById(dto.getAssignedTo()).orElseThrow(() ->
                new EntityNotFoundException("no assignee with id: " + dto.getAssignedTo()));
        Task task = Task.builder()
                .title(dto.getTitle())
                .priority(TaskPriority.valueOf(dto.getPriority()))
                .status(TaskStatus.valueOf(dto.getStatus()))
                .createdBy(creator)
                .assignedTo(assignee)
                .build();

        return _taskMapper.toTaskDto(_taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long id, UpdateTaskRequestDTO dto) {
        Task task = _taskRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("no task with id: " + id));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));

        if (!task.getAssignedTo().getId().equals(dto.getAssignedTo())) {
            task.setAssignedTo(_userRepository.findById(dto.getAssignedTo())
                    .orElseThrow(() ->
                            new EntityNotFoundException("no assignee with id: " + dto.getAssignedTo())));
        }

        return _taskMapper.toTaskDto(task);
    }

    public void deleteTask(Long id) {
        _taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task found with id: " + id));

        _taskRepository.deleteById(id);
    }
}
