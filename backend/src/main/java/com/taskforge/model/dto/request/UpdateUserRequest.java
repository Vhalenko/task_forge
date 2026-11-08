package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest (
        @NotBlank String firstName,
        @NotBlank String lastName,
        String username,
        @NotBlank String email) {
}
