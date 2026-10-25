package com.taskforge.service;

import com.taskforge.model.dto.request.LoginRequestDTO;
import com.taskforge.model.dto.request.RegisterRequestDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO register(RegisterRequestDTO dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.valueOf(dto.getRole()))
                .build();

        return userRepository.save(user).toDto();
    }

    public UserDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("no user with email: " + dto.getEmail()));

        if (user.getPasswordHash().equals(passwordEncoder.encode(dto.getPassword()))) {
            return user.toDto();
        }
        else {
            throw new EntityNotFoundException("incorrect password or email");
        }
    }
}
