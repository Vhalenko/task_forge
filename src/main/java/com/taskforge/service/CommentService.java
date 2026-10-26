package com.taskforge.service;

import ch.qos.logback.classic.spi.LoggingEventVO;
import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.UpdateCommentRequest;
import com.taskforge.model.entity.Comment;
import com.taskforge.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public CommentDTO update(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no comment with id: " + id));

        comment.setText(request.getText());
        return commentRepository.save(comment).toDto();
    }
}
