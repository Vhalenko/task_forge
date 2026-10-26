package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateTaskRequest (
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String priority,
        @NotBlank String status,
        @NotBlank LocalDateTime completedAt) {
}
