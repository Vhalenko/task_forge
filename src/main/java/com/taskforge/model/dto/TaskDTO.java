package com.taskforge.model.dto;

import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class TaskDTO {
    private final Long id;
    private final String title;
    private final String description;
    private final TaskPriority priority;
    private final TaskStatus status;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;
}
