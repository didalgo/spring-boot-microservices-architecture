package com.idalgo.daniel.contracts.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order DTO for consistent order representation across services.
 * 
 * Used when:
 * - Order Service returns order details
 * - Other services need order information
 * 
 * Example JSON:
 * {
 *   "orderId": "ORD-1234567890",
 *   "customerId": "CUST-001",
 *   "productId": "PROD-001",
 *   "quantity": 2,
 *   "totalAmount": 199.98,
 *   "status": "CONFIRMED",
 *   "paymentId": "PAY-9876543210",
 *   "createdAt": "2024-02-13T10:30:00"
 * }
 * 
 * @param orderId Unique order identifier
 * @param customerId Customer who placed the order
 * @param productId Product being ordered
 * @param quantity Number of units
 * @param totalAmount Total cost
 * @param status Current order status
 * @param paymentId Associated payment ID (null if payment not yet processed)
 * @param createdAt When order was created
 */
public record OrderDTO(
    
    String orderId,
    String customerId,
    String productId,
    Integer quantity,
    BigDecimal totalAmount,
    OrderStatus status,
    String paymentId,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {
}
