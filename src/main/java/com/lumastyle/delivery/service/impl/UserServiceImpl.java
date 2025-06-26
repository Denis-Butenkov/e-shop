package com.lumastyle.delivery.service.impl;

import com.lumastyle.delivery.dto.UserRequest;
import com.lumastyle.delivery.dto.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;
import com.lumastyle.delivery.mapper.UserMapper;
import com.lumastyle.delivery.repository.UserRepository;
import com.lumastyle.delivery.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserResponse registerUser(UserRequest request) {
        UserEntity newUserEntity = mapper.toEntity(request);
        newUserEntity = repository.save(newUserEntity);
        log.info("User registered successfully: {}", request.getEmail());
        return mapper.toResponse(newUserEntity);
    }

}
