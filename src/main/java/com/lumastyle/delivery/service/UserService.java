package com.lumastyle.delivery.service;

import com.lumastyle.delivery.dto.UserRequest;
import com.lumastyle.delivery.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

}
