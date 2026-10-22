package com.taskforge.exception;

import com.taskforge.model.dto.ExceptionResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ExceptionResponseDTO> buildMessage(HttpStatus status, Exception e) {
        return ResponseEntity.status(status)
                .body(new ExceptionResponseDTO(
                        status.value(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGlobal(Exception e) {
        return buildMessage(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleNotFound(EntityNotFoundException e) {
        return buildMessage(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponseDTO> handleDataAccess(DataAccessException e) {
        return buildMessage(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}
