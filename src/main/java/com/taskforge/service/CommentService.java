package com.taskforge.service;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public CommentDTO update(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no comment with id: " + id));

        comment.setText(request.text());
        return commentRepository.save(comment).toDto();
    }
}
