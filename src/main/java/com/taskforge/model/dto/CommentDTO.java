package com.taskforge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDTO {
    private final Long id;
    private final String text;
    private final LocalDateTime createdAt;
}
