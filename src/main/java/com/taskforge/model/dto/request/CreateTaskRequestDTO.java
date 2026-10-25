package com.taskforge.model.dto.request;

import lombok.Data;

@Data
public class CreateTaskRequestDTO {
    private String title;
    private String description;
    private String priority;
    private String status;
    private Long createdBy;
    private Long assignedTo;
}
