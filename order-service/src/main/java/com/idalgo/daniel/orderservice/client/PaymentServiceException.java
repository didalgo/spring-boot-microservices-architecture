package com.idalgo.daniel.orderservice.client;

/**
 * Exception thrown when communication with Payment Service fails.
 *
 * Covers:
 * - Network errors (timeout, connection refused)
 * - HTTP errors (4xx, 5xx)
 * - Deserialization errors
 */
public class PaymentServiceException extends RuntimeException {

    public PaymentServiceException(String message) {
        super(message);
    }

    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}