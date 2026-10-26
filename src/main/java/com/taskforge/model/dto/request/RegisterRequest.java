package com.taskforge.model.dto.request;

public record RegisterRequest (
        String firstName,
        String lastName,
        String username,
        String email,
        String password) {
}
