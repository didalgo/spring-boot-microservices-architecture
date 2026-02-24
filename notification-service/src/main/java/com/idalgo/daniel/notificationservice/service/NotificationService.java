package com.idalgo.daniel.notificationservice.service;

import com.idalgo.daniel.contracts.events.OrderConfirmedEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    /**
     * Sends notification for confirmed order.
     * 
     * In production, this would:
     * - Send email via SendGrid/AWS SES
     * - Send SMS via Twilio
     * - Send push notification
     * 
     * For now, we simulate with logging.
     */
    public void sendOrderConfirmedNotification(OrderConfirmedEvent event) {
        log.info("=".repeat(60));
        log.info("📧 SENDING NOTIFICATION");
        log.info("To: Customer {}", event.customerId());
        log.info("Subject: Order {} Confirmed!", event.orderId());
        log.info("Message: Your order of ${} has been confirmed.", event.totalAmount());
        log.info("Payment ID: {}", event.paymentId());
        log.info("=".repeat(60));
        
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}