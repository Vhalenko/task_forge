package com.taskforge.exception;

public record ExceptionResponseDTO (
        int status,
        String exception,
        String message
) {
}
