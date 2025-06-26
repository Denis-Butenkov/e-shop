package com.lumastyle.delivery.mapper.impl;

import com.lumastyle.delivery.dto.UserRequest;
import com.lumastyle.delivery.dto.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;
import com.lumastyle.delivery.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final PasswordEncoder encoder;

    @Override
    public UserEntity toEntity(UserRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
    }

    @Override
    public UserResponse toResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .build();
    }
}
