package com.idalgo.daniel.contracts.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public record PaymentDTO(
    
    String paymentId,
    String orderId,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String customerEmail,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime processedAt
) {
}
