package com.idalgo.daniel.notificationservice.listener;

import com.idalgo.daniel.contracts.events.OrderConfirmedEvent;
import com.idalgo.daniel.notificationservice.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    
    private final NotificationService notificationService;
    
    /**
     * Listens to order-events topic and processes OrderConfirmedEvent.
     * 
     * This method is idempotent - processing same event twice is safe.
     */
    @KafkaListener(
        topics = "order-events",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Received OrderConfirmedEvent: eventId={}, orderId={}", 
            event.eventId(), event.orderId());
        
        try {
            notificationService.sendOrderConfirmedNotification(event);
            log.info("Successfully processed event: {}", event.eventId());
        } catch (Exception e) {
            log.error("Error processing event: {}", event.eventId(), e);
            throw e; // Re-throw to trigger Kafka retry
        }
    }
}