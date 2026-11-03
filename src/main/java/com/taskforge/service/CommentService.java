package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void delete(Long id, Long callerId) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(callerId)) {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }

        commentRepository.deleteById(id);
    }

    public CommentDTO update(Long id, CommentRequest request, Long callerId) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(callerId)) {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }

        comment.setText(request.text());
        return commentRepository.save(comment).toDto();
    }
}
