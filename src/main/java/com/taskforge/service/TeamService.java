package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.CreateProjectRequest;
import com.taskforge.model.dto.request.CreateTeamRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, ProjectRepository projectRepository) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
    }

    public List<TeamDTO> findAll() {
        return teamRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Team::getId))
                .map(Team::toDto)
                .toList();
    }

    public TeamDTO findById(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no team with id: " + id));
        return team.toDto();
    }

    public TeamDTO save(CreateTeamRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .build();
        return teamRepository.save(team).toDto();
    }

    public void delete(Long id) {
        teamRepository.deleteById(id);
    }

    public List<UserDTO> findUsers(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no team with id: " + id));

        return team.getUsers()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(User::toDto)
                .toList();
    }

    public List<ProjectDTO> findProjects(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no team with id: " + id));

        return team.getProjects()
                .stream()
                .sorted(Comparator.comparing(Project::getId))
                .map(Project::toDto)
                .toList();
    }

    public ProjectDTO saveProject(Long id, CreateProjectRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("no team with id: " + id));

        Project project = Project.builder()
                .name(request.getName())
                .team(team)
                .build();

        return projectRepository.save(project).toDto();
    }
}
