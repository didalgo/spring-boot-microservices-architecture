package com.idalgo.daniel.contracts.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response DTO used across all microservices.
 * 
 * Provides consistent error response format for API clients.
 * 
 * Design principles:
 * - Consistent structure across all services
 * - Machine-readable error codes
 * - Human-readable messages
 * - Optional validation details
 * - Timestamp for debugging
 * 
 * Example JSON:
 * {
 *   "errorCode": "ORDER_NOT_FOUND",
 *   "message": "Order not found with ID: ORD-123",
 *   "timestamp": "2024-02-13T10:30:00",
 *   "path": "/api/orders/ORD-123",
 *   "validationErrors": null
 * }
 * 
 * @param errorCode Machine-readable error code (e.g., ORDER_NOT_FOUND)
 * @param message Human-readable error message
 * @param timestamp When the error occurred
 * @param path Request path that caused the error (optional)
 * @param validationErrors List of field validation errors (optional, for 400 Bad Request)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    
    String errorCode,
    
    String message,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    
    String path,
    
    List<ValidationError> validationErrors
) {
    
    /**
     * Constructor for simple errors without validation details.
     */
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now(), null, null);
    }
    
    /**
     * Constructor for errors with path but no validation details.
     */
    public ErrorResponse(String errorCode, String message, String path) {
        this(errorCode, message, LocalDateTime.now(), path, null);
    }
}
