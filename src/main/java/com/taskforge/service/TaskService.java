package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.dto.request.UpdateTaskRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.repository.CommentRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public List<TaskDTO> findAll() {
        return this.taskRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(Task::toDto)
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));
        return task.toDto();
    }

    @Transactional
    public TaskDTO update(Long id, UpdateTaskRequest request, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        Task task = taskRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("Task not found"));

        if (!task.getCreatedBy().equals(caller)) {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setDueDate(request.dueDate());

        if (request.status().equals(TaskStatus.DONE)) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return task.toDto();
    }

    public void delete(Long id, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        Task task = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));

        if (!task.getCreatedBy().equals(caller)) {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }

        taskRepository.deleteById(id);
    }

    @Transactional
    public void assign(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        if (!task.getAssignees().add(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already assigned");
        }
    }

    public List<CommentDTO> findComments(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));

        return task.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(Comment::toDto)
                .toList();
    }

    public CommentDTO saveComment(Long taskId, Long userId, CommentRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .text(request.text())
                .createdAt(LocalDateTime.now())
                .task(task)
                .user(user)
                .build();
        return  commentRepository.save(comment).toDto();
    }
}
