package com.taskforge.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ExceptionResponse> buildMessage(HttpStatus status, Exception e) {
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(
                        status.value(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGlobal(Exception e) {
        return buildMessage(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(EntityNotFoundException e) {
        return buildMessage(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleDataAccess(DataAccessException e) {
        return buildMessage(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> badCredentials(BadCredentialsException e) {
        return buildMessage(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> locked(LockedException e) {
        return buildMessage(HttpStatus.FORBIDDEN, e);
    }
}
