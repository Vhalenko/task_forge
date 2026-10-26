package com.taskforge.model.dto.request;

public record CreateTaskRequest (
         String title,
         String description,
         String priority,
         String status,
         Long createdBy,
         Long assignedTo) {
}
