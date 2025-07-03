package com.lumastyle.eshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.auth.AuthRequest;
import com.lumastyle.eshop.dto.auth.AuthResponse;
import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.repository.UserRepository;
import com.lumastyle.eshop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthController.
 * These tests validate the /api/login endpoint under various scenarios.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }


    /**
     * Test: login with valid credentials should return a JWT token.
     */
    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        userRepository.save(UserEntity.builder()
                .fullName("Charlie")
                .email("charlie@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        AuthRequest req = new AuthRequest();
        req.setEmail("charlie@example.com");
        req.setPassword("password123");

        String responseBody = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse res = objectMapper.readValue(responseBody, AuthResponse.class);
        assertEquals("charlie@example.com", jwtUtil.extractUsername(res.getToken()));
    }

    /**
     * Test: login with invalid credentials should return a server error.
     */
    @Test
    void login_withInvalidCredentials_returnsServerError() throws Exception {
        userRepository.save(UserEntity.builder()
                .fullName("Dan")
                .email("dan@example.com")
                .password(passwordEncoder.encode("correctpass"))
                .build());

        AuthRequest req = new AuthRequest();
        req.setEmail("dan@example.com");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    /**
     * Test: missing email in a request should return BadRequest (400).
     */
    @Test
    void login_withMissingEmail_returnsBadRequest() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setPassword("password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: missing password in the request should return BadRequest (400).
     */
    @Test
    void login_withMissingPassword_returnsBadRequest() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setEmail("user@example.com");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}