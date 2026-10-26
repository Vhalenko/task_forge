package com.taskforge.controller;

import com.taskforge.model.dto.AuthDTO;
import com.taskforge.model.dto.request.LoginRequest;
import com.taskforge.model.dto.request.RegisterRequest;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.register(request));
    }
    @GetMapping("/login")
    public ResponseEntity<AuthDTO> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }
}
