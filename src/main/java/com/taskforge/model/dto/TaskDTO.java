package com.taskforge.model.dto;

import java.time.LocalDateTime;


public record TaskDTO(
        Long id,
        String title,
        String description,
        String priority,
        String status,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt) {
}
