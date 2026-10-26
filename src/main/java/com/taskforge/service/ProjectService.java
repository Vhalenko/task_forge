package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.CreateTaskRequest;
import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectDTO findById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + id));

        return project.toDto();
    }

    public List<UserDTO> findUsers(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + id));

        return project.getUsers()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(User::toDto)
                .toList();
    }

    public List<TaskDTO> findTasks(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + id));

        return project.getTasks()
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(Task::toDto)
                .toList();
    }

    public ProjectDTO update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + id));

        project.setName(request.name());
        return projectRepository.save(project).toDto();
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public TaskDTO saveTask(Long projectId, Long userId, CreateTaskRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + projectId));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("no project with id: " + userId));

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TODO)
                .priority(TaskPriority.valueOf(request.priority()))
                .project(project)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build();

        return taskRepository.save(task).toDto();
    }
}
