package com.lumastyle.eshop.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil.
 * Uses reflection to set @Value-injected fields.
 */
class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp() throws Exception {
        // Generate a secure 256-bit key for HS256
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        // Inject the Base64-encoded key into the correct field
        Field secretField = JwtUtil.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, base64Key);

        // Set expiration to 1 hour
        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 1000L * 60 * 60);
    }

    /**
     * Test: generating and validating a token for a given UserDetails.
     */
    @Test
    void generateAndValidateToken() {
        String username = "test@example.com";
        UserDetails userDetails = new User(username, "", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        assertEquals(username, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    /**
     * Test: extracting from a malformed token should throw.
     */
    @Test
    void invalidTokenThrowsExceptionOnExtract() {
        String badToken = "malformed.token.value";
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(badToken));
    }
}
