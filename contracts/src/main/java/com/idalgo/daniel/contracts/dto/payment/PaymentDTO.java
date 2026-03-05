package com.idalgo.daniel.contracts.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment response DTO.
 * 
 * Returned when:
 * - Processing a payment (201 Created)
 * - Retrieving a payment (200 OK)
 * - Listing payments (200 OK with array)
 * - Refunding a payment (200 OK)
 * 
 * Example JSON:
 * {
 *   "paymentId": "PAY-1234567890",
 *   "orderId": "ORD-123",
 *   "amount": 199.98,
 *   "paymentMethod": "CREDIT_CARD",
 *   "status": "COMPLETED",
 *   "customerEmail": "customer@example.com",
 *   "createdAt": "2024-02-13T10:30:00",
 *   "processedAt": "2024-02-13T10:30:05"
 * }
 * 
 * @param paymentId Unique payment identifier
 * @param orderId Associated order ID
 * @param amount Payment amount
 * @param paymentMethod Payment method used
 * @param status Current payment status
 * @param customerEmail Customer's email
 * @param createdAt When payment was created
 * @param processedAt When payment was processed (null if still PENDING)
 */
@Schema(description = "Payment information for an order")
public record PaymentDTO(

    @Schema(
            description = "Unique payment identifier",
            example = "PAY-1677849601-1"
    )    
    String paymentId,

    @Schema(
            description = "Order ID associated with this payment",
            example = "ORD-1677849600-1",
            required = true
    )
    String orderId,

    @Schema(
            description = "Payment amount in USD",
            example = "199.99",
            required = true
    )
    BigDecimal amount,
    
    @Schema(
            description = "Payment method used",
            example = "CREDIT_CARD",
            allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"},
            required = true
    )
    PaymentMethod paymentMethod,

    @Schema(
            description = "Payment status",
            example = "SUCCESS",
            allowableValues = {"PENDING", "SUCCESS", "FAILED"},
            required = true
    )
    PaymentStatus status,

    @Schema(
            description = "Customer email address for payment confirmation",
            example = "customer@example.com",
            format = "email",
            required = true
    )
    String customerEmail,

    @Schema(
            description = "Payment creation timestamp",
            example = "2024-03-05T10:30:00",
            type = "string",
            format = "date-time"
    )    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @Schema(
            description = "Payment processing timestamp",
            example = "2024-03-05T10:30:01",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime processedAt
) {
}
