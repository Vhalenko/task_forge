package com.taskforge.exception;

import java.time.Instant;
import java.util.Map;

public record ExceptionResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, String> fieldErrors
) {
}
