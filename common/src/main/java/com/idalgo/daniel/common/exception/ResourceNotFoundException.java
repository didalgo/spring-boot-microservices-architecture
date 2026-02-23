package com.idalgo.daniel.common.exception;

/**
 * Base exception for resource not found scenarios.
 * 
 * Services can extend this for specific not found exceptions:
 * - OrderNotFoundException extends ResourceNotFoundException
 * - PaymentNotFoundException extends ResourceNotFoundException
 * 
 * This allows GlobalExceptionHandler to handle all not found
 * exceptions with a single @ExceptionHandler method.
 * 
 * Will result in HTTP 404 Not Found when thrown from a controller.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceType;
    private final String resourceId;
    
    /**
     * Constructor with resource type and ID.
     * 
     * @param resourceType Type of resource (e.g., "Order", "Payment")
     * @param resourceId ID of the resource that wasn't found
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    /**
     * Constructor with custom message.
     * 
     * @param message Custom error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Resource";
        this.resourceId = "unknown";
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public String getResourceId() {
        return resourceId;
    }
}
