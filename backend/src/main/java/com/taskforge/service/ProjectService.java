package com.taskforge.service;

import com.taskforge.constant.CacheNames;
import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CreateTaskRequest;
import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectDTO findById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Project not found"));

        return project.toDto();
    }

    @Cacheable(value = CacheNames.TASKS_BY_PROJECT, key = "#id")
    public List<TaskDTO> findTasks(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found");
        }

        return taskRepository.findByProject_IdOrderByIdAsc(id)
                .stream()
                .map(Task::toDto)
                .toList();
    }

    public ProjectDTO update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Project not found"));

        project.setName(request.name());
        return projectRepository.save(project).toDto();
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    @CacheEvict(value = CacheNames.TASKS_BY_PROJECT, key = "#projectId")
    public TaskDTO saveTask(Long projectId, Long callerId, CreateTaskRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException("Project not found"));

        User user = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TODO)
                .priority(request.priority())
                .project(project)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .dueDate(request.dueDate())
                .build();

        Task savedTask = taskRepository.save(task);

        log.info("New task created: taskId={}", savedTask.getId());

        return savedTask.toDto();
    }
}
