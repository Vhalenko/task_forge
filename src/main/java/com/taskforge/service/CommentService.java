package com.taskforge.service;

import com.taskforge.repository.CommentRepository;
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
}
