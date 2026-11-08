package com.taskforge.model.dto;

public record AuthDTO(
        String token,
        UserDTO user) {
}
