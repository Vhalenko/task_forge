package com.taskforge.service;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.dto.request.UpdateUserStatusRequest;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.TeamRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
        return user.toDto();
    }

    public List<TaskDTO> findTasks(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }

        return taskRepository.findDistinctByAssignees_IdOrderByIdAsc(id)
                .stream()
                .map(Task::toDto)
                .toList();
    }

    @Transactional
    public void updateRole(Long id, UpdateRoleRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        user.setRole(request.role());
    }

    @Transactional
    public void updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        user.setBlocked(request.isBlocked());

        if (request.isBlocked()) {
            log.info("User was blocked; userId={}", user.getId());
        }
    }

    public UserDTO update(UpdateUserRequest request, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        caller.setFirstName(request.firstName());
        caller.setLastName(request.lastName());
        caller.setEmail(request.email().toLowerCase());

        if (!request.username().isBlank()) {
            caller.setUsername(request.username());
        }

        return userRepository.save(caller).toDto();
    }

    @Transactional
    public void updateTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("Team not found"));

        user.setTeam(team);
    }

    public UserDTO updateMe(@Valid UpdateUserRequest request, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + callerId));

        caller.setFirstName(request.firstName());
        caller.setLastName(request.lastName());
        caller.setUsername(request.username());
        caller.setEmail(request.email());

        return userRepository.save(caller).toDto();
    }
}
