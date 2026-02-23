package com.idalgo.daniel.orderservice.exception;

import com.idalgo.daniel.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when an order is not found.
 * 
 * Now extends ResourceNotFoundException from common module,
 * so it's automatically handled by GlobalExceptionHandler.
 * 
 * Results in HTTP 404 Not Found with error code "ORDER_NOT_FOUND".
 * 
 * Migration note:
 * This replaces the previous standalone OrderNotFoundException.
 * No changes needed in service or controller code.
 */
public class OrderNotFoundException extends ResourceNotFoundException {
    
    /**
     * Constructor with order ID.
     * 
     * @param orderId The ID of the order that was not found
     */
    public OrderNotFoundException(String orderId) {
        super("Order", orderId);
    }
}
