package com.idalgo.daniel.contracts.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(
        description = "Payment status in the payment lifecycle. " +
                "Valid transitions: PENDING→COMPLETED, PENDING→FAILED, COMPLETED→REFUNDED",
        example = "COMPLETED"
)
public enum PaymentStatus {

    /**
     * Payment created, waiting to be processed.
     */
    @Schema(description = "Payment created, waiting to be processed")
    PENDING,

    /**
     * Payment successfully processed.
     */
    @Schema(description = "Payment successfully processed and funds captured")
    COMPLETED,

    /**
     * Payment failed or was rejected.
     */
    @Schema(description = "Payment failed or was rejected by payment gateway")
    FAILED,

    /**
     * Payment was refunded to customer.
     * Can only transition from COMPLETED.
     */
    @Schema(description = "Payment was refunded to customer (only from COMPLETED status)")
    REFUNDED
}
