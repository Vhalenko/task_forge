package com.taskforge.controller;

import com.taskforge.model.dto.UserDTO;
import com.taskforge.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("api/users")
public class UserController {
    private final UserService _userService;
    public UserController(UserService userService) {
        _userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(_userService.getById(id));
    }
}
