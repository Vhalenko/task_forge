package com.taskforge.service;

import com.taskforge.mapper.TaskMapper;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository _taskRepository;
    private final TaskMapper _taskMapper;
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {

        _taskRepository = taskRepository;
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

    public TaskDTO createTask(Task task) {

        Task createdTask = _taskRepository.save(task);
        return _taskMapper.toTaskDto(createdTask);
    }

    public TaskDTO updateTask(Task task) {

        Task updatedTask = _taskRepository.save(task);
        return _taskMapper.toTaskDto(updatedTask);
    }

    public void deleteTask(Long id) {
        _taskRepository.deleteById(id);
    }
}
