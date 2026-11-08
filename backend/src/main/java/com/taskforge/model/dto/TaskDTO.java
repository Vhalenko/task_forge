package com.taskforge.model.dto;

import java.time.LocalDateTime;
import java.util.List;


public record TaskDTO(
        Long id,
        String title,
        String description,
        String priority,
        String status,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        Long createdBy,
        List<UserDTO> assignees,
        Long projectId,
        String projectName) {
}
