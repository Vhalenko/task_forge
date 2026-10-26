package com.taskforge.model.dto.request;

import java.time.LocalDateTime;

public record UpdateTaskRequest (
        String title,
        String description,
        String priority,
        String status,
        LocalDateTime completedAt) {
}
