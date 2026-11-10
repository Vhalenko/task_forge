package com.taskforge.service;

import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.ProjectRequest;
import com.taskforge.model.dto.request.TeamRequest;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TeamRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Team> redisTemplate;


    public List<TeamDTO> findAll() {
        return teamRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Team::getId))
                .map(Team::toDto)
                .toList();
    }

    public TeamDTO findById(Long id) {
        log.info("getting team {}", id);
        Team teamFromCache = redisTemplate.opsForValue()
                .get("teamCache:" + id);

        if (teamFromCache != null) {
            return teamFromCache.toDto();
        }

        Team team = teamRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Team not found"));

        redisTemplate.opsForValue().set("teamCache:" + id, team, 1, TimeUnit.MINUTES);
        return team.toDto();
    }

    @CachePut(value = "team", key = "#result.id")
    public TeamDTO save(TeamRequest request) {
        Team team = Team.builder()
                .name(request.name())
                .build();
        return teamRepository.save(team).toDto();
    }

    @CacheEvict(value = "team", key = "id")
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }

    public List<UserDTO> findUsers(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found");
        }

        return userRepository.findByTeam_IdOrderByIdAsc(id)
                .stream()
                .map(User::toDto)
                .toList();
    }

    public List<ProjectDTO> findProjects(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found");
        }

        return projectRepository.findByTeam_IdOrderByIdAsc(id)
                .stream()
                .map(Project::toDto)
                .toList();
    }

    public ProjectDTO saveProject(Long id, ProjectRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        Project project = Project.builder()
                .name(request.name())
                .team(team)
                .build();

        return projectRepository.save(project).toDto();
    }
}
