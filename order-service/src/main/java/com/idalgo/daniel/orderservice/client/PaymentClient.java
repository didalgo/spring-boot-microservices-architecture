package com.idalgo.daniel.orderservice.client;

import com.idalgo.daniel.contracts.dto.payment.PaymentDTO;
import com.idalgo.daniel.contracts.dto.payment.ProcessPaymentRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
            String jwt = getJwtFromRequest();

            if (jwt == null || jwt.isEmpty()) {
                log.warn("No JWT token found in request");
                throw new PaymentServiceException("Authentication required");
            }            

            PaymentDTO response = paymentServiceRestClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)  // Propagar JWT
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

    /**
     * Extract JWT from current HTTP request
     */
    private String getJwtFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Remover "Bearer "
        }

        return null;
    }    
}