package com.idalgo.daniel.contracts.dto.payment;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for processing a payment.
 * 
 * Used when creating a new payment transaction.
 * 
 * Example JSON:
 * {
 *   "orderId": "ORD-123",
 *   "amount": 199.98,
 *   "paymentMethod": "CREDIT_CARD",
 *   "customerEmail": "customer@example.com",
 *   "cardNumber": "4111111111111111"
 * }
 * 
 * @param orderId Associated order ID
 * @param amount Payment amount (must be positive)
 * @param paymentMethod Method of payment
 * @param customerEmail Customer's email for receipt
 * @param cardNumber Card number (only for card payments, validated with pattern)
 */
public record ProcessPaymentRequest(
    
    @NotBlank(message = "Order ID is required")
    String orderId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have max 10 digits and 2 decimals")
    BigDecimal amount,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    String customerEmail,
    
    @Pattern(
        regexp = "^[0-9]{13,19}$",
        message = "Card number must be 13-19 digits"
    )
    String cardNumber  // Optional, only for card payments
) {
}
