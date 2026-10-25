package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
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
}
