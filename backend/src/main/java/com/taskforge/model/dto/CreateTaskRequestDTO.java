package com.taskforge.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequestDTO {
    private String title;
    private String description;
    private String priority;
    private String status;
    private Long createdBy;
    private Long assignedTo;
}
