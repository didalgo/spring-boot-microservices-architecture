package com.idalgo.daniel.contracts.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when an order is confirmed.
 *
 * This event is immutable and self-contained.
 */
public record OrderConfirmedEvent(
        String eventId,
        String orderId,
        String customerId,
        BigDecimal totalAmount,
        String paymentId,
        LocalDateTime timestamp
) {

    /**
     * Factory method to create event with auto-generated eventId.
     */
    public static OrderConfirmedEvent create(
            String orderId,
            String customerId,
            BigDecimal totalAmount,
            String paymentId
    ) {
        return new OrderConfirmedEvent(
                UUID.randomUUID().toString(),
                orderId,
                customerId,
                totalAmount,
                paymentId,
                LocalDateTime.now()
        );
    }
}