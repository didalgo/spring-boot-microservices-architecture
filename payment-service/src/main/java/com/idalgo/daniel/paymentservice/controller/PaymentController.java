package com.idalgo.daniel.paymentservice.controller;

import com.idalgo.daniel.contracts.dto.payment.*;
import com.idalgo.daniel.paymentservice.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Payment Management.
 * <p>
 * Base path: /api/payments
 * <p>
 * Endpoints:
 * - POST   /api/payments             - Process payment (201 Created)
 * - GET    /api/payments             - List all payments (200 OK)
 * - GET    /api/payments/{id}        - Get specific payment (200 OK)
 * - PUT    /api/payments/{id}/refund - Refund payment (200 OK)
 * <p>
 * Error handling:
 * - All exceptions are handled by GlobalExceptionHandler from common module
 * - Returns consistent ErrorResponse format
 * - Proper HTTP status codes (404, 400, etc.)
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment processing and management operations")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Processes a new payment.
     * <p>
     * HTTP POST /api/payments
     * <p>
     * Request body: ProcessPaymentRequest (JSON)
     * Response: PaymentDTO with 201 Created
     *
     * @param request Payment processing request
     * @return Processed payment with HTTP 201
     */
    @Operation(
            summary = "Process a new payment",
            description = "Processes a payment for an order using the specified payment method. " +
                    "The payment is initially created in PENDING status and transitions to " +
                    "COMPLETED or FAILED based on payment gateway response."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment processed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful payment",
                                    value = """
                                            {
                                              "paymentId": "PAY-1677849601-1",
                                              "orderId": "ORD-1677849600-1",
                                              "amount": 199.99,
                                              "paymentMethod": "CREDIT_CARD",
                                              "customerEmail": "customer@example.com",
                                              "status": "COMPLETED",
                                              "createdAt": "2024-03-05T10:30:00",
                                              "timestamp": "2024-03-05T10:30:01",
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment request (validation failed)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Payment processing failed (payment gateway error)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Payment failed",
                                    value = """
                                            {
                                              "paymentId": "PAY-1677849601-1",
                                              "orderId": "ORD-1677849600-1",
                                              "amount": 199.99,
                                              "paymentMethod": "CREDIT_CARD",
                                              "customerEmail": "customer@example.com",
                                              "status": "FAILED",
                                              "createdAt": "2024-03-05T10:30:00",
                                              "timestamp": "2024-03-05T10:30:01",
                                              "errorMessage": "Insufficient funds"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request
    ) {
        log.info("Received request to process payment for order: {}", request.orderId());

        PaymentDTO processedPayment = paymentService.processPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(processedPayment);
    }

    /**
     * Retrieves all payments.
     * <p>
     * HTTP GET /api/payments
     * <p>
     * Response: List of PaymentDTO with 200 OK
     *
     * @return List of all payments
     */
    @Operation(
            summary = "Get all payments",
            description = "Retrieves a list of all payments in the system across all statuses"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of payments retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.info("Received request to list all payments");

        List<PaymentDTO> payments = paymentService.getAllPayments();

        log.debug("Returning {} payments", payments.size());

        return ResponseEntity.ok(payments);
    }

    /**
     * Retrieves a specific payment by ID.
     * <p>
     * HTTP GET /api/payments/{paymentId}
     * <p>
     * Path variable: paymentId
     * Response: PaymentDTO with 200 OK
     *
     * @param paymentId Payment ID to retrieve
     * @return Payment details
     * @throws PaymentNotFoundException if payment not found (handled by GlobalExceptionHandler → 404)
     */
    @Operation(
            summary = "Get payment by ID",
            description = "Retrieves detailed information about a specific payment"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @Parameter(
                    description = "Payment ID in format PAY-{timestamp}-{sequence}",
                    example = "PAY-1677849601-1",
                    required = true
            )
            @PathVariable String paymentId
    ) {
        log.info("Received request to get payment: {}", paymentId);

        PaymentDTO payment = paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(payment);
    }

    /**
     * Processes a refund for a payment.
     * <p>
     * HTTP PUT /api/payments/{paymentId}/refund
     * <p>
     * Path variable: paymentId
     * Request body: RefundRequest (JSON)
     * Response: PaymentDTO with 200 OK
     * <p>
     * Business rules enforced:
     * - Payment must exist (404 if not)
     * - Payment must be COMPLETED (400 if not)
     *
     * @param paymentId Payment ID to refund
     * @param request   Refund request with reason
     * @return Refunded payment
     * @throws PaymentNotFoundException     if payment not found
     * @throws InvalidPaymentStateException if payment cannot be refunded
     */
    @Operation(
            summary = "Refund a payment",
            description = "Refunds a completed payment and returns funds to customer. " +
                    "Only payments in COMPLETED status can be refunded. " +
                    "The payment status will transition from COMPLETED to REFUNDED."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment refunded successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class),
                            examples = @ExampleObject(
                                    name = "Refunded payment",
                                    value = """
                                            {
                                              "paymentId": "PAY-1677849601-1",
                                              "orderId": "ORD-1677849600-1",
                                              "amount": 199.99,
                                              "paymentMethod": "CREDIT_CARD",
                                              "customerEmail": "customer@example.com",
                                              "status": "REFUNDED",
                                              "createdAt": "2024-03-05T10:30:00",
                                              "timestamp": "2024-03-05T10:30:05",
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refund request (validation failed or invalid payment status)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "Payment must be in COMPLETED status to be refunded. Current status: FAILED",
                                              "timestamp": "2024-03-05T10:30:00",
                                              "path": "/api/payments/PAY-1677849601-1/refund"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(
            @Parameter(
                    description = "Payment ID to refund",
                    example = "PAY-1677849601-1",
                    required = true
            )
            @PathVariable String paymentId,

            @Valid @RequestBody RefundRequest request
    ) {
        log.info("Received request to refund payment: {}", paymentId);

        PaymentDTO refundedPayment = paymentService.refundPayment(paymentId, request);

        return ResponseEntity.ok(refundedPayment);
    }
}
