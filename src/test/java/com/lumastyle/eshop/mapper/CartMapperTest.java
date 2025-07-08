package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartMapperTest {

    private final CartMapper mapper = Mappers.getMapper(CartMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        CartEntity entity = CartEntity.builder()
                .id("1")
                .userId("u1")
                .items(Map.of("p", 2))
                .build();

        CartResponse response = mapper.toResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getUserId(), response.getUserId());
        assertEquals(entity.getItems(), response.getItems());
    }
}
