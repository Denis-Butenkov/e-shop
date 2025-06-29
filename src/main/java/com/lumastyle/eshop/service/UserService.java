package com.lumastyle.eshop.service;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;

/**
 * User registration and lookup operations.
 */
public interface UserService {

    /**
     * Register a new user account from the given request data.
     *
     * @param request the new user’s details (email, password, etc.)
     * @return the created user’s summary DTO
     */
    UserResponse registerUser(UserRequest request);

    /**
     * Get the currently logged-in user’s ID.
     *
     * @return the authenticated user’s database ID
     */
    String getCurrentUserId();

}
