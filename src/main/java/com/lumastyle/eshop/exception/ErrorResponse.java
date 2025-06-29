package com.lumastyle.eshop.exception;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response payload for all API errors.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code
 * @param error     short description of the error type
 * @param message   detailed error message
 * @param path      request URI that caused the error
 * @param details   list of validation or field-specific error messages
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {}
