package com.idalgo.daniel.paymentservice.exception;

import com.idalgo.daniel.contracts.dto.payment.PaymentStatus;

/**
 * Exception thrown when attempting an invalid payment state transition.
 * 
 * For example:
 * - Trying to refund a PENDING payment (must be COMPLETED)
 * - Trying to refund a FAILED payment
 * - Trying to refund an already REFUNDED payment
 * 
 * This will be handled by GlobalExceptionHandler as a generic exception
 * and return HTTP 400 Bad Request.
 */
public class InvalidPaymentStateException extends RuntimeException {
    
    private final String paymentId;
    private final PaymentStatus currentStatus;
    private final String attemptedAction;
    
    /**
     * Constructor with payment details.
     * 
     * @param paymentId ID of the payment
     * @param currentStatus Current status of the payment
     * @param attemptedAction Action that was attempted
     */
    public InvalidPaymentStateException(
        String paymentId,
        PaymentStatus currentStatus,
        String attemptedAction
    ) {
        super(String.format(
            "Cannot %s payment %s in status %s",
            attemptedAction,
            paymentId,
            currentStatus
        ));
        this.paymentId = paymentId;
        this.currentStatus = currentStatus;
        this.attemptedAction = attemptedAction;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public PaymentStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public String getAttemptedAction() {
        return attemptedAction;
    }
}
