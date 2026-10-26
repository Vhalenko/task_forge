package com.taskforge.controller;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.UpdateRoleRequest;
import com.taskforge.model.dto.request.UpdateUserRequest;
import com.taskforge.model.dto.request.UpdateUserStatusRequest;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findById(principal.getUser().getId()));
    }

    @GetMapping("/me/tasks")
    public ResponseEntity<List<TaskDTO>> findMyTasks(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findTasks(principal.getUser().getId()));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        userService.updateRole(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/blocked")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateUserStatusRequest request) {
        userService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/teams/{teamId}")
    public ResponseEntity<Void> updateTeam(@PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "teamId") Long teamId) {
        userService.updateTeam(userId, teamId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.update(id, request));
    }
}
