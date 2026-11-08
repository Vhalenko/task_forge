package com.taskforge.model.dto.request;

import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateTaskRequest (
        @NotBlank String title,
        String description,
        @NotNull TaskPriority priority,
        @NotNull TaskStatus status,
        LocalDateTime dueDate) {
}
