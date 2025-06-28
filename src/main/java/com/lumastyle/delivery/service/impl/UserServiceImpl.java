package com.lumastyle.delivery.service.impl;

import com.lumastyle.delivery.dto.user.UserRequest;
import com.lumastyle.delivery.dto.user.UserResponse;
import com.lumastyle.delivery.entity.UserEntity;
import com.lumastyle.delivery.exception.BadRequestException;
import com.lumastyle.delivery.mapper.UserMapper;
import com.lumastyle.delivery.repository.UserRepository;
import com.lumastyle.delivery.service.AuthFacade;
import com.lumastyle.delivery.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final AuthFacade authFacade;

    @Override
    public UserResponse registerUser(UserRequest request) {
        isUniqueValidation(request);
        UserEntity newUserEntity = mapper.toEntity(request);
        newUserEntity = repository.save(newUserEntity);
        log.info("User registered successfully: {}", request.getEmail());
        return mapper.toResponse(newUserEntity);
    }

    @Override
    public String getCurrentUserId() {
        String loggedInUserEmail = authFacade.getAuthentication().getName();
        UserEntity loggedInUser = repository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new IllegalStateException("User not found: " + loggedInUserEmail));
        log.info("User found successfully: {}", loggedInUserEmail);
        return loggedInUser.getId();
    }

    // === Helper methods ===

    /**
     * Validates that the requested email is not already in use.
     * Throws BadRequestException if a user with that email exists.
     *
     * @param request the incoming UserRequest
     * @throws BadRequestException if email is already taken
     */
    private void isUniqueValidation(UserRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException(
                    "User with email: '" + request.getEmail() + "' already exists"
            );
        }
    }

}
