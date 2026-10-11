package com.taskforge.mapper;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public Task toTask(TaskDTO taskDTO, User createdBy, User assignedTo, List<Comment> comments) {
        return new Task(
                taskDTO.getId(),
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                TaskPriority.valueOf(taskDTO.getPriority()),
                TaskStatus.valueOf(taskDTO.getStatus()),
                taskDTO.getCreatedAt(),
                taskDTO.getCompletedAt(),
                createdBy,
                assignedTo,
                comments
        );
    }
}
