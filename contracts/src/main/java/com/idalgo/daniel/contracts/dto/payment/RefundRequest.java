package com.idalgo.daniel.contracts.dto.payment;

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
public record RefundRequest(
    
    @NotBlank(message = "Refund reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    String reason
) {
}
