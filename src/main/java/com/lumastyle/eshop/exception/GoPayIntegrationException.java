package com.lumastyle.eshop.exception;

import java.io.Serial;

public class GoPayIntegrationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public GoPayIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoPayIntegrationException(String message) {
        super(message);
    }
}
