package com.lumastyle.eshop.exception;

/**
 * Exception thrown when a client request is invalid or cannot be processed.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message explaining why the request is invalid
     */
    public BadRequestException(String message) { super(message); }
}
