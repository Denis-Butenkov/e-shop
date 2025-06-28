package com.lumastyle.delivery.service;

import com.lumastyle.delivery.dto.user.UserRequest;
import com.lumastyle.delivery.dto.user.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    String getCurrentUserId();

}
