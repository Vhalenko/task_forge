package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequest (
         @NotBlank String title,
         @NotBlank String description,
         @NotBlank String priority) {
}
