package com.lumastyle.eshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Global exception handler delivering uniform error responses across all controllers.
 * <p>
 * Catches specific application exceptions and transforms them into structured
 * {@link ErrorResponse} payloads with appropriate HTTP status codes.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom {@link BadRequestException} thrown by service or controller logic.
     *
     * @param ex      the BadRequestException instance
     * @param request the HttpServletRequest that resulted in the exception
     * @return ResponseEntity containing an {@link ErrorResponse} with status 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} raised on validation failures
     * for {@code @Valid}-annotated request bodies.
     *
     * @param ex      the MethodArgumentNotValidException instance
     * @param request the HttpServletRequest that resulted in the exception
     * @return ResponseEntity containing an {@link ErrorResponse} with status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = collectFieldErrors(ex);

        String combined = String.join("; ", errors);

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                combined,
                request.getRequestURI(),
                errors       // field-specific messages
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }


    /**
     * Handles {@link ResourceNotFoundException} for missing resources.
     *
     * @param ex      the ResourceNotFoundException instance
     * @param request the HttpServletRequest that resulted in the exception
     * @return ResponseEntity containing an {@link ErrorResponse} with status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    /**
     * Handles {@link FileStorageException} for errors during file operations.
     *
     * @param ex      the FileStorageException instance
     * @param request the HttpServletRequest that resulted in the exception
     * @return ResponseEntity containing an {@link ErrorResponse} with status 500
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(
            FileStorageException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "File Storage Error",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    /**
     * Handles exceptions thrown during GoPay integration.
     *
     * @param ex      the GoPayIntegrationException containing details about the integration failure
     * @param request the HTTP request that resulted in the exception
     * @return a ResponseEntity containing an ErrorResponse with HTTP 502 Bad Gateway status
     */
    @ExceptionHandler(GoPayIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleGoPayIntegration(
            GoPayIntegrationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "GoPay Integration Error",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(body);
    }

    /**
     * Handles all uncaught exceptions as internal server errors.
     *
     * @param ex      the Exception instance
     * @param request the HttpServletRequest that resulted in the exception
     * @return ResponseEntity containing an {@link ErrorResponse} with status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    // === Helper methods ===

    /**
     * Collects validation errors from a MethodArgumentNotValidException into a list of field error messages.
     *
     * @param ex the MethodArgumentNotValidException containing a binding result with field errors
     * @return a list of formatted error messages in the form "fieldName: errorMessage"
     */
    private static List<String> collectFieldErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format(
                        "%s: %s",
                        fieldError.getField(),
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
                ))
                .toList();
    }
}
