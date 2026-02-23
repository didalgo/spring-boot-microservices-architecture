package com.idalgo.daniel.contracts.dto.order;

/**
 * Order lifecycle states.
 * 
 * State transitions:
 * PENDING → PAYMENT_PROCESSING (when calling payment service)
 * PAYMENT_PROCESSING → CONFIRMED (payment successful)
 * PAYMENT_PROCESSING → PAYMENT_FAILED (payment rejected or service unavailable)
 * CONFIRMED → CANCELLED (user cancels confirmed order)
 * 
 * Invalid transitions will throw exceptions.
 */
public enum OrderStatus {
    
    /**
     * Order created, awaiting payment processing.
     */
    PENDING,
    
    /**
     * Currently processing payment with payment service.
     * Transient state.
     */
    PAYMENT_PROCESSING,
    
    /**
     * Payment successful, order confirmed.
     */
    CONFIRMED,
    
    /**
     * Payment failed or was rejected.
     */
    PAYMENT_FAILED,
    
    /**
     * Order was cancelled by user or system.
     */
    CANCELLED
}
