package com.idalgo.daniel.contracts.dto.common;

/**
 * Represents a single field validation error.
 * 
 * Used in ErrorResponse when request validation fails (HTTP 400).
 * 
 * Example JSON:
 * {
 *   "field": "amount",
 *   "rejectedValue": "-10.50",
 *   "message": "Amount must be positive"
 * }
 * 
 * @param field Name of the field that failed validation
 * @param rejectedValue The value that was rejected (as string)
 * @param message Validation error message
 */
public record ValidationError(
    String field,
    String rejectedValue,
    String message
) {
}
