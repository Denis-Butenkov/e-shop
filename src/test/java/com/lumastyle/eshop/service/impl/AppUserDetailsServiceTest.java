package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppUserDetailsService.
 */
@ActiveProfiles("test")
class AppUserDetailsServiceTest {

    private final UserRepository repository = Mockito.mock(UserRepository.class);
    private final AppUserDetailsService service = new AppUserDetailsService(repository);

    /**
     * Test: loading existing user.
     */
    @Test
    void loadUserByUsername_exists() {
        UserEntity user = new UserEntity();
        user.setEmail("exists@example.com");
        user.setPassword("pass");
        Mockito.when(repository.findByEmail("exists@example.com")).thenReturn(java.util.Optional.of(user));

        UserDetails details = service.loadUserByUsername("exists@example.com");
        assertEquals("exists@example.com", details.getUsername());
    }

    /**
     * Test: loading non-existent user throws.
     */
    @Test
    void loadUserByUsername_notFound() {
        Mockito.when(repository.findByEmail("nope@example.com")).thenReturn(java.util.Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nope@example.com"));
    }
}

