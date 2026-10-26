package com.taskforge.model.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime completedAt;
}
