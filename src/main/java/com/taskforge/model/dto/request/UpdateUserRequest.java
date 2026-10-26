package com.taskforge.model.dto.request;

public record UpdateUserRequest (
        String firstName,
        String lastName,
        String username,
        String email) {
}
