package com.taskforge.exception;

public record ExceptionResponse(
        int status,
        String exception,
        String message
) {
}
