package com.idalgo.daniel.paymentservice.exception;

import com.idalgo.daniel.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when a payment is not found.
 * 
 * Extends ResourceNotFoundException so it's automatically handled
 * by GlobalExceptionHandler from the common module.
 * 
 * Results in HTTP 404 Not Found with error code "PAYMENT_NOT_FOUND".
 */
public class PaymentNotFoundException extends ResourceNotFoundException {
    
    /**
     * Constructor with payment ID.
     * 
     * @param paymentId The ID of the payment that was not found
     */
    public PaymentNotFoundException(String paymentId) {
        super("Payment", paymentId);
    }
}
