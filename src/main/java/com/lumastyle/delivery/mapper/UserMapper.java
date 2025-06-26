package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.UserRequest;
import com.lumastyle.delivery.dto.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;

public interface UserMapper {

    UserEntity toEntity (UserRequest request);

    UserResponse toResponse(UserEntity entity);
}
