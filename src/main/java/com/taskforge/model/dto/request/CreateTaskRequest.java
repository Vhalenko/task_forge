package com.taskforge.model.dto.request;

import com.taskforge.model.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateTaskRequest (
        @NotBlank String title,
        String description,
        @NotNull TaskPriority priority,
        LocalDateTime dueDate) {
}
