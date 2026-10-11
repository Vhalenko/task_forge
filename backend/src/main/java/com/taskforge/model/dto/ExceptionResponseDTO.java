package com.taskforge.model.dto;

public record ExceptionResponseDTO (
        int status,
        String exception,
        String message
) {
}
