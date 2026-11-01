package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.User;
import com.taskforge.repository.CommentRepository;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void delete(Long id, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("no user with id: " + callerId));

        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no comment with id: " + id));

        if (!comment.getUser().equals(caller)) {
            throw new AccessDeniedException("you cannot delete comments of other users");
        }

        commentRepository.deleteById(id);
    }

    public CommentDTO update(Long id, CommentRequest request, Long callerId) {
        User caller = userRepository.findById(callerId).orElseThrow(() ->
                new EntityNotFoundException("no user with id: " + callerId));

        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no comment with id: " + id));

        if (!comment.getUser().equals(caller)) {
            throw new AccessDeniedException("you cannot delete comments of other users");
        }

        comment.setText(request.text());
        return commentRepository.save(comment).toDto();
    }
}
