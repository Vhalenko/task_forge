package com.taskforge.controller;

import com.taskforge.model.dto.CommentDTO;
import com.taskforge.model.dto.request.CommentRequest;
import com.taskforge.security.CurrentUserId;
import com.taskforge.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PutMapping("{id}")
    public ResponseEntity<CommentDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody CommentRequest request,
                                             @CurrentUserId Long callerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.update(id, request, callerId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUserId Long callerId) {
        commentService.delete(id, callerId);
        return ResponseEntity.noContent().build();
    }
}
