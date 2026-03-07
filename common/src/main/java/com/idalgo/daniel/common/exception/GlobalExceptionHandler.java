package com.idalgo.daniel.common.exception;

import com.idalgo.daniel.contracts.dto.common.ErrorResponse;
import com.idalgo.daniel.contracts.dto.common.ValidationError;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global exception handler for all microservices.
 * 
 * Provides consistent error responses across all services.
 * 
 * Handles:
 * - ResourceNotFoundException (404)
 * - MethodArgumentNotValidException (400 - validation errors)
 * - Generic exceptions (500)
 * 
 * Usage:
 * Services just need to include the 'common' module as a dependency
 * and throw exceptions. This handler will automatically catch them
 * and return appropriate HTTP responses.
 * 
 * @RestControllerAdvice applies to all @RestController classes
 * @Slf4j provides logging
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handles resource not found exceptions.
     * 
     * Returns HTTP 404 with structured error response.
     * 
     * @param ex The ResourceNotFoundException
     * @param request HTTP request for path extraction
     * @return 404 response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request
    ) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        String errorCode = ex.getResourceType().toUpperCase() + "_NOT_FOUND";
        
        ErrorResponse error = new ErrorResponse(
            errorCode,
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI(),
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }
    
    /**
     * Handles request validation errors.
     * 
     * Returns HTTP 400 with list of field validation errors.
     * 
     * Triggered by @Valid or @Validated on request bodies.
     * 
     * @param ex The MethodArgumentNotValidException
     * @param request HTTP request for path extraction
     * @return 400 response with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        
        List<ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::mapFieldError)
            .toList();
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_FAILED",
            "Request validation failed",
            LocalDateTime.now(),
            request.getRequestURI(),
            validationErrors
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }
    
    /**
     * Handles all other unexpected exceptions.
     * 
     * Returns HTTP 500 with generic error message.
     * Logs full stack trace for debugging.
     * 
     * @param ex The unexpected exception
     * @param request HTTP request for path extraction
     * @return 500 response with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred. Please try again later.",
            LocalDateTime.now(),
            request.getRequestURI(),
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
    
    /**
     * Maps a Spring FieldError to our ValidationError DTO.
     * 
     * @param fieldError Spring's field error
     * @return Our ValidationError DTO
     */
    private ValidationError mapFieldError(FieldError fieldError) {
        String rejectedValue = fieldError.getRejectedValue() != null
            ? fieldError.getRejectedValue().toString()
            : "null";
        
        return new ValidationError(
            fieldError.getField(),
            rejectedValue,
            fieldError.getDefaultMessage()
        );
    }

    /**
     * Handle Access Denied (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request
    ) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "FORBIDDEN",
                "You don't have permission to access this resource",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
