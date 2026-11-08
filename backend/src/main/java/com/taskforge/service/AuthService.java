package com.taskforge.service;

import com.taskforge.model.dto.AuthDTO;
import com.taskforge.model.dto.request.LoginRequest;
import com.taskforge.model.dto.request.RegisterRequest;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.repository.UserRepository;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDTO register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("Registration rejected because email is already registered");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is in use");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(request.firstName() + " " + request.lastName());
        }

        User savedUser = userRepository.save(user);

        log.info(
                "User registered: userId={}",
                savedUser.getId()
        );

        return savedUser.toDto();
    }

    public AuthDTO login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Incorrect email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.info("Authentication failed");
            throw new BadCredentialsException("Incorrect email or password");
        }

        if (user.isBlocked()) {
            throw new LockedException("Account is blocked");
        }

        log.info(
                "User authenticated: userId={}",
                user.getId()
        );

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthDTO(token, user.toDto());
    }
}
