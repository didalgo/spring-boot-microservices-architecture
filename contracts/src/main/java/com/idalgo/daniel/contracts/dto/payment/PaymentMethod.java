package com.idalgo.daniel.contracts.dto.payment;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Available payment methods")
public enum PaymentMethod {
    
    /**
     * Credit card payment.
     */
    @Schema(description = "Credit card payment")
    CREDIT_CARD,
    
    /**
     * Debit card payment.
     */
    @Schema(description = "Debit card payment")
    DEBIT_CARD,
    
    /**
     * PayPal payment.
     */
    @Schema(description = "PayPal payment")
    PAYPAL,
    
    /**
     * Bank transfer payment.
     */
    @Schema(description = "Bank transfer payment")
    BANK_TRANSFER
}
