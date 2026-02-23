package com.idalgo.daniel.contracts.dto.payment;

/**
 * Payment lifecycle states.
 * 
 * State transitions:
 * PENDING → COMPLETED (payment successful)
 * PENDING → FAILED (payment rejected)
 * COMPLETED → REFUNDED (payment refunded)
 * 
 * Invalid transitions will throw InvalidPaymentStateException.
 */
public enum PaymentStatus {
    
    /**
     * Payment created, waiting to be processed.
     */
    PENDING,
    
    /**
     * Payment successfully processed.
     */
    COMPLETED,
    
    /**
     * Payment failed or was rejected.
     */
    FAILED,
    
    /**
     * Payment was refunded to customer.
     * Can only transition from COMPLETED.
     */
    REFUNDED
}
