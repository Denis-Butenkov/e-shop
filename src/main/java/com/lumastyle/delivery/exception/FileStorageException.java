package com.lumastyle.delivery.exception;

/**
 * Exception thrown when file storage operations fail.
 */
public class FileStorageException extends RuntimeException{

    /**
     * Constructs a new FileStorageException with the specified detail message and cause.
     *
     * @param message the detail message explaining the storage failure
     * @param cause   the cause of this exception
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FileStorageException with the specified detail message.
     *
     * @param message the detail message explaining the storage failure
     */
    public FileStorageException(String message) {
        super(message);
    }
}
