package com.taskforge.controller;

import com.taskforge.model.dto.AuthDTO;
import com.taskforge.model.dto.request.LoginRequestDTO;
import com.taskforge.model.dto.request.RegisterRequestDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.register(dto));
    }
    @GetMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(dto));
    }
}
