package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CreateCommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequestDTO;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskDTO> findAll() {
        return this.taskRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(Task::toDto)
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task with id: " + id));
        return task.toDto();
    }

    @Transactional
    public TaskDTO update(Long id, UpdateTaskRequestDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("no task with id: " + id));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));

        return task.toDto();
    }

    public void delete(Long id) {
        taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task found with id: " + id));

        taskRepository.deleteById(id);
    }

    @Transactional
    public void updateAssignee(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("no task found with id: " + taskId));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("no task found with id: " + userId));

        if (!task.getAssignees().add(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user is already assigned");
        }
    }

    public List<CommentDTO> findComments(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task with id: " + id));

        return task.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(Comment::toDto)
                .toList();
    }

    public CommentDTO saveComment(Long id, CreateCommentRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no task with id: " + id));

        Comment comment = new Comment().builder()
                .text(request.getText())
                .createdAt(LocalDateTime.now())
                .task(task)
                .build();
        return comment.toDto();
    }
}
