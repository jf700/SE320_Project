package com.digitaltherapy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = Map.of(
            "error", Map.of(
                "code", "INTERNAL_ERROR",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            )
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = Map.of(
            "error", Map.of(
                "code", "VALIDATION_ERROR",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            )
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
