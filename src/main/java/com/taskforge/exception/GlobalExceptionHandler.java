package com.taskforge.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected server error: requestId={}, path={}",
                MDC.get("requestId"),
                request.getRequestURI(),
                ex
        );

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage())
                                .orElse("Invalid value"),
                        (first, second) -> first
                ));

        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                request,
                errors
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.debug(
                "Resource not found: method={}, path={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return error(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request,
                Map.of()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.info(
                "Authentication failed: method={}, path={}",
                request.getMethod(),
                request.getRequestURI());

        return error(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Incorrect email or password",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> locked(LockedException ex,
                                                    HttpServletRequest request) {

        log.warn(
                "Account locked: method={}, path={}, userId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId")
        );

        return error(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                ex.getMessage(),
                request,
                Map.of()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn(
                "Access denied: method={}, path={}, userId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId")
        );

        return error(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "You do not have permission to perform this action",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Data conflict: method={}, path={}, userId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId")
        );

        return error(
                HttpStatus.CONFLICT,
                "CONFLICT",
                "The requested resource conflicts with existing data",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        log.info(
                "Request failed: method={}, path={}, userId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId")
        );

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        return error(
                status,
                status == HttpStatus.CONFLICT ? "CONFLICT" : "REQUEST_ERROR",
                ex.getReason() != null ? ex.getReason() : "Request failed",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.debug(
                "Invalid request body: method={}, path={}, userId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId")
        );

        return error(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Request body is invalid",
                request,
                Map.of()
        );
    }

    private ResponseEntity<ExceptionResponse> error(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {

        String traceId = Optional.ofNullable(
                request.getHeader("X-Request-ID")
        ).orElse(UUID.randomUUID().toString());

        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                status.value(),
                code,
                message,
                request.getRequestURI(),
                traceId,
                fieldErrors
        );

        return ResponseEntity.status(status).body(response);
    }
}
