package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Mapper interface for converting between {@link UserRequest}/{@link UserResponse} DTOs and {@link UserEntity} entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link UserRequest} DTO into a {@link UserEntity} for persistence.
     * <p>
     * The provided plain-text password is encoded using the supplied
     * {@link PasswordEncoder} before being set on the entity.
     * <p>
     * Note: The generated entity's {@code id} is left null, as it will be assigned by the database upon saving.
     *
     * @param request the {@link UserRequest} DTO containing user registration data (e.g., {@code fullName}, {@code email}, {@code password})
     * @param passwordEncoder encoder used to hash the password
     * @return a new {@link UserEntity} populated from the request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    UserEntity toEntity(UserRequest request, @Context PasswordEncoder passwordEncoder);

    /**
     * Converts a {@link UserEntity} into a {@link UserResponse} DTO for client consumption.
     * <p>
     * The mapper intentionally ignores the entity's {@code id} to avoid exposing internal database identifiers
     * in API responses.
     *
     * @param entity the {@link UserEntity} retrieved from the database
     * @return a {@link UserResponse} DTO containing user data (without the internal {@code id} field)
     */
    @Mapping(target = "id", ignore = true)
    UserResponse toResponse(UserEntity entity);
}
