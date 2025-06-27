package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.user.UserRequest;
import com.lumastyle.delivery.dto.user.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;

public interface UserMapper {

    UserEntity toEntity (UserRequest request);

    UserResponse toResponse(UserEntity entity);
}
