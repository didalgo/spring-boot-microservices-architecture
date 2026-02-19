package com.idalgo.daniel.orderservice.exception;

/**
 * Exception thrown when an order is not found.
 * 
 * This is a business exception that should result in HTTP 404 Not Found.
 * 
 * In a production application, you would typically have a
 * @ControllerAdvice class to handle this exception globally
 * and return appropriate HTTP responses.
 * 
 * For now, Spring Boot's default exception handling will convert
 * this to a 500 error, but in Lesson 3 we'll implement proper
 * exception handling.
 */
public class OrderNotFoundException extends RuntimeException {
    
    /**
     * Constructor with order ID.
     * 
     * @param orderId The ID of the order that was not found
     */
    public OrderNotFoundException(String orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    /**
     * Constructor with custom message.
     * 
     * @param message Custom error message
     */
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
