package com.idalgo.daniel.orderservice.client;

import com.idalgo.daniel.contracts.dto.payment.PaymentDTO;
import com.idalgo.daniel.contracts.dto.payment.ProcessPaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

/**
 * Client for communicating with Payment Service.
 *
 * Encapsulates all HTTP communication with payment-service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {

    private final RestClient paymentServiceRestClient;

    /**
     * Processes a payment by calling Payment Service.
     *
     * @param request Payment request
     * @return Payment response
     * @throws PaymentServiceException if communication fails
     */
    public PaymentDTO processPayment(ProcessPaymentRequest request) {
        log.info("Calling Payment Service to process payment for order: {}", request.orderId());

        try {
            PaymentDTO response = paymentServiceRestClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentDTO.class);

            log.info("Payment Service responded: paymentId={}, status={}",
                    response.paymentId(), response.status());

            return response;

        } catch (RestClientException e) {
            log.error("Failed to communicate with Payment Service", e);
            throw new PaymentServiceException(
                    "Payment Service communication failed: " + e.getMessage(),
                    e
            );
        }
    }
}