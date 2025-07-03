package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
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

    @Test
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
