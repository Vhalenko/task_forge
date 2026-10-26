package com.taskforge.model.dto;

import java.time.LocalDateTime;

public record CommentDTO(
        Long id,
        String text,
        LocalDateTime createdAt) {
}
