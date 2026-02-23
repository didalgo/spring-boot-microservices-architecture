package com.idalgo.daniel.contracts.dto.payment;

/**
 * Supported payment methods.
 * 
 * Shared enum used across services that handle payments.
 * 
 * Adding new payment methods:
 * 1. Add enum value here
 * 2. Update payment processing logic in PaymentService
 * 3. Update validation rules if needed
 * 4. Update API documentation
 */
public enum PaymentMethod {
    
    /**
     * Credit card payment.
     */
    CREDIT_CARD,
    
    /**
     * Debit card payment.
     */
    DEBIT_CARD,
    
    /**
     * PayPal payment.
     */
    PAYPAL,
    
    /**
     * Bank transfer payment.
     */
    BANK_TRANSFER
}
