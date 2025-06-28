package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.user.UserRequest;
import com.lumastyle.delivery.dto.user.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;

public interface UserMapper {

    /**
     * Converts a UserRequest DTO to a UserEntity.
     *
     * @param request the user request DTO containing registration data
     * @return the mapped UserEntity
     */
    UserEntity toEntity (UserRequest request);

    /**
     * Converts a UserEntity to a UserResponse DTO.
     *
     * @param entity the user entity retrieved from the database
     * @return the mapped UserResponse DTO for client consumption
     */
    UserResponse toResponse(UserEntity entity);
}
