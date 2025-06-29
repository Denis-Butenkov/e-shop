package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;

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
