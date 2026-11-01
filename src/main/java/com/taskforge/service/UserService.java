package com.taskforge.service;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

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

        user.setRole(request.role());
    }

    @Transactional
    public void updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        user.setBlocked(request.isBlocked());
    }

    public UserDTO update(Long id, UpdateUserRequest request, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + callerId));

        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id));

        if (!user.equals(caller)) {
            throw new AccessDeniedException("you cannot edit other users");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());

        return userRepository.save(user).toDto();
    }

    @Transactional
    public void updateTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + userId));

        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + teamId));

        user.setTeam(team);
    }
}
