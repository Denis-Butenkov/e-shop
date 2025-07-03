package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AppUserDetailsService}, verifying its behavior
 * when loading user details by username.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private AppUserDetailsService service;

    /**
     * Test: loading an existing user returns correct {@link UserDetails}.
     */
    @Test
    @DisplayName("loadUserByUsername returns UserDetails for existing user")
    @Tag("Unit")
    void loadUserByUsername_exists() {
        UserEntity user = new UserEntity();
        user.setEmail("exists@example.com");
        user.setPassword("pass");
        when(repository.findByEmail("exists@example.com"))
                .thenReturn(java.util.Optional.of(user));

        UserDetails details = service.loadUserByUsername("exists@example.com");

        assertEquals("exists@example.com", details.getUsername());
        assertEquals("pass", details.getPassword());
    }

    /**
     * Test: loading a non-existent user throws {@link UsernameNotFoundException}.
     */
    @Test
    @DisplayName("loadUserByUsername throws when user not found")
    @Tag("Unit")
    void loadUserByUsername_notFound() {
        when(repository.findByEmail("nope@example.com"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("nope@example.com"));
    }
}
