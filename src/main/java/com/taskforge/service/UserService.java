package com.taskforge.service;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateTeamRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.dto.request.UpdateUserStatusRequest;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.repository.TeamRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public UserService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));
        return user.toDto();
    }

    public List<TaskDTO> findTasks(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        return user.getAssignedTasks()
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(Task::toDto)
                .toList();
    }

    @Transactional
    public void updateRole(Long id, UpdateRoleRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        user.setRole(UserRole.valueOf(request.getRole()));
    }

    @Transactional
    public void updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        user.setBlocked(request.isBlocked());
    }

    public UserDTO update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return userRepository.save(user).toDto();
    }

    @Transactional
    public void updateTeam(Long id, UpdateTeamRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        Team team = teamRepository.findById(request.getId()).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + request.getId()));

        user.setTeam(team);
    }
}
