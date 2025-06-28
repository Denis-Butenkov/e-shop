package com.lumastyle.delivery.service;

import org.springframework.security.core.Authentication;

/**
 * A thin facade over Spring Security’s Authentication subsystem,
 * exposing only what the application needs.
 */
public interface AuthFacade {

    /**
     * Return the current {@link org.springframework.security.core.Authentication}
     * (i.e., the logged-in user’s principal and authorities).
     *
     * @return the current authentication token
     */
    Authentication getAuthentication();
}
