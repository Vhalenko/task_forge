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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDTO register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is in use");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        if (request.username().isBlank()) {
            user.setUsername(request.firstName() + " " + request.lastName());
        }

        return userRepository.save(user).toDto();
    }

    public AuthDTO login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Incorrect email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Incorrect email or password");
        }

        if (user.isBlocked()) {
            throw new LockedException("Account is blocked");
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthDTO(token, user.toDto());
    }
}
