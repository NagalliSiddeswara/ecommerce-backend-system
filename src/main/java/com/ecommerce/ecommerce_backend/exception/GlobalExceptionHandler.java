package com.ecommerce.ecommerce_backend.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException ex) {

        String message = ex.getMessage();

        // Duplicate email
        if ("Email is already registered".equals(message)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "Email is already registered"
                    ));
        }

        // Invalid login credentials
        if ("Invalid email or password".equals(message)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid email or password"
                    ));
        }

        // Resource not found
        if ("Product not found".equals(message)
                || "Order not found".equals(message)
                || "Cart item not found".equals(message)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", message
                    ));
        }

        // Insufficient stock
        if (message != null
                && message.startsWith("Insufficient")) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", message
                    ));
        }

        // Other invalid requests
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", "Invalid request"
                ));
    }
}