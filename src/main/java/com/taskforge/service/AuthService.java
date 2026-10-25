package com.taskforge.service;

import com.taskforge.model.dto.AuthDTO;
import com.taskforge.model.dto.request.LoginRequestDTO;
import com.taskforge.model.dto.request.RegisterRequestDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.repository.UserRepository;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO register(RegisterRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is in use");
        }

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(email)
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.USER)
                .build();

        return userRepository.save(user).toDto();
    }

    public AuthDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("incorrect email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new EntityNotFoundException("incorrect password or email");
        }

        if (user.isBlocked()) {
            throw new LockedException("Account is blocked");
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthDTO(token, user.toDto());
    }
}
