package com.taskforge.model.dto;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String role) {
}
