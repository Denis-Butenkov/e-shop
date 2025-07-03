package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.exception.BadRequestException;
import com.lumastyle.eshop.mapper.UserMapper;
import com.lumastyle.eshop.repository.UserRepository;
import com.lumastyle.eshop.service.AuthFacade;
import com.lumastyle.eshop.service.UserService;
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
    private final AuthFacade authFacade;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) {
        isUniqueValidation(request);
        UserEntity newUserEntity = mapper.toEntity(request, passwordEncoder);
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
