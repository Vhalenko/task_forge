package com.taskforge.controller;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.dto.request.UpdateUserStatusRequest;
import com.taskforge.security.CurrentUserId;
import com.taskforge.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe(@CurrentUserId Long callerId) {
        return ResponseEntity.ok(userService.findById(callerId));
    }

    @GetMapping("/me/tasks")
    public ResponseEntity<List<TaskDTO>> findMyTasks(@CurrentUserId Long callerId) {
        return ResponseEntity.ok(userService.findTasks(callerId));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        userService.updateRole(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/blocked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }

//    @PatchMapping("/{userId}/teams/{teamId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> updateTeam(@PathVariable(name = "userId") Long userId,
//                                           @PathVariable(name = "teamId") Long teamId) {
//        userService.updateTeam(userId, teamId);
//        return ResponseEntity.noContent().build();
//    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UpdateUserRequest request,
                                          @CurrentUserId Long callerId) {
        return ResponseEntity.ok(userService.update(request, callerId));
    }
}
