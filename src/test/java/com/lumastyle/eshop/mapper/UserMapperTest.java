package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserMapper}, verifying mapping between  {@link UserRequest},
 * {@link UserEntity} and {@link UserResponse}, including password encoding behavior.
 *
 */
@ActiveProfiles("test")
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final PasswordEncoder encoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return "ENC:" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    };

    /**
     * Verifies that toEntity maps fields and encodes the password correctly.
     */
    @Test
    @DisplayName("toEntity maps fields and encodes password")
    @Tag("Unit")
    void toEntity_mapsFieldsAndEncodesPassword() {
        UserRequest request = UserRequest.builder()
                .fullName("User")
                .email("u@test.com")
                .password("pass")
                .build();

        UserEntity entity = mapper.toEntity(request, encoder);

        assertNull(entity.getId());
        assertEquals(request.getFullName(), entity.getFullName());
        assertEquals(request.getEmail(), entity.getEmail());
        assertEquals("ENC:" + request.getPassword(), entity.getPassword());
    }

    /**
     * Verifies that toResponse ignores the entity ID and maps other fields correctly.
     */
    @Test
    @DisplayName("toResponse ignores ID and maps fields correctly")
    @Tag("Unit")
    void toResponse_ignoresId() {
        UserEntity entity = UserEntity.builder()
                .id("1")
                .fullName("User")
                .email("u@test.com")
                .password("secret")
                .build();

        UserResponse response = mapper.toResponse(entity);

        assertNull(response.getId());
        assertEquals(entity.getFullName(), response.getFullName());
        assertEquals(entity.getEmail(), response.getEmail());
    }
}
