package com.taskforge.service;

import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
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
}
