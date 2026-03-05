package com.idalgo.daniel.contracts.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for refunding a payment.
 *
 * Example JSON:
 * {
 *   "reason": "Customer requested refund due to product defect"
 * }
 *
 * @param reason Reason for the refund (required for audit trail)
 */
@Schema(description = "Request to refund a completed payment")
public record RefundRequest(

        @Schema(
                description = "Reason for the refund (required for audit trail and compliance)",
                example = "Customer requested refund due to product defect",
                minLength = 10,
                maxLength = 500,
                required = true
        )
        @NotBlank(message = "Refund reason is required")
        @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
        String reason
) {
}