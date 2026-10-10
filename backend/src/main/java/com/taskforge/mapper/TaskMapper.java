package com.taskforge.mapper;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDTO toTaskDto(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority().toString(),
                task.getStatus().toString(),
                task.getCreatedAt(),
                task.getCompletedAt(),
                task.getCreatedBy().getId(),
                task.getAssignedTo().getId()
        );
    }
}
