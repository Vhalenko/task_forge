package com.taskforge.controller;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.UpdateCommentRequest;
import com.taskforge.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PutMapping("{id}")
    public ResponseEntity<CommentDTO> update(@PathVariable Long id, @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.update(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
