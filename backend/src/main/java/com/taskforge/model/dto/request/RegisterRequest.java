package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
        @NotBlank String firstName,
        @NotBlank String lastName,
        String username,
        @NotBlank String email,
        @NotBlank String password) {
}
