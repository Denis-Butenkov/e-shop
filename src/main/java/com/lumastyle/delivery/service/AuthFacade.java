package com.lumastyle.delivery.service;

import org.springframework.security.core.Authentication;

public interface AuthFacade {

    Authentication getAuthentication();
}
