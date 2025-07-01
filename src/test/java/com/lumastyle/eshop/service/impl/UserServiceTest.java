package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.exception.BadRequestException;
import com.lumastyle.eshop.mapper.UserMapper;
import com.lumastyle.eshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest request;
    private UserEntity entity;
    private UserEntity savedEntity;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        request = new UserRequest();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Simulate mapper encoding password
        entity = UserEntity.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password("encodedPassword")
                .build();

        savedEntity = UserEntity.builder()
                .id("user-id-1")
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password("encodedPassword")
                .build();

        response = UserResponse.builder()
                .id(savedEntity.getId())
                .fullName(savedEntity.getFullName())
                .email(savedEntity.getEmail())
                .build();
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldThrowBadRequest() {
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(entity));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.registerUser(request));

        assertEquals("User with email: 'test@example.com' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toEntity(any());
    }

    @Test
    void registerUser_whenValid_shouldMapEntityAndSave() {
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(response);

        UserResponse result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("user-id-1", result.getId());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());

        verify(userMapper).toEntity(request);
        verify(userRepository).save(entity);
        verify(userMapper).toResponse(savedEntity);
    }
}