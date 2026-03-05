package com.idalgo.daniel.contracts.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to process a payment for an order")
public record ProcessPaymentRequest(

        @Schema(
                description = "Order ID to process payment for",
                example = "ORD-1677849600-1",
                required = true
        )
        @NotBlank(message = "Order ID is required")
        String orderId,

        @Schema(
                description = "Payment amount in USD",
                example = "199.99",
                type = "number",
                format = "decimal",
                required = true,
                minimum = "0.01",
                maximum = "9999999999.99"
        )
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have max 10 digits and 2 decimals")
        BigDecimal amount,

        @Schema(
                description = "Payment method to use",
                example = "CREDIT_CARD",
                required = true,
                implementation = PaymentMethod.class
        )
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Schema(
                description = "Customer email address for payment confirmation",
                example = "customer@example.com",
                format = "email",
                required = true
        )
        @NotBlank(message = "Customer email is required")
        @Email(message = "Invalid email format")
        String customerEmail,

        @Schema(
                description = "Credit/debit card number (required only for card payments)",
                example = "4532015112830366",
                pattern = "^[0-9]{13,19}$",
                minLength = 13,
                maxLength = 19,
                nullable = true
        )
        @Pattern(
                regexp = "^[0-9]{13,19}$",
                message = "Card number must be 13-19 digits"
        )
        String cardNumber  // Optional, only for card payments
) {
}
