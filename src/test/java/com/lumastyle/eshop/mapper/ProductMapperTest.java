package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ProductMapper},
 * verifying mapping between {@link ProductRequest}, {@link ProductEntity}and {@link ProductResponse}.
 */
@ActiveProfiles("test")
class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    /**
     * Verifies that toEntity maps fields from {@link ProductRequest} to {@link ProductEntity} correctly.
     */
    @Test
    @DisplayName("toEntity maps all fields correctly")
    @Tag("Unit")
    void toEntity_mapsFields() {
        ProductRequest request = new ProductRequest("Name", "Desc", new BigDecimal("250.50"), "Cat");

        ProductEntity entity = mapper.toEntity(request);

        assertNull(entity.getId());
        assertNull(entity.getImageUrl());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getDescription(), entity.getDescription());
        assertEquals(request.getPrice(), entity.getPrice());
        assertEquals(request.getCategory(), entity.getCategory());
    }

    /**
     * Verifies that toResponse maps fields from {@link ProductEntity} to {@link ProductResponse} correctly.
     */
    @Test
    @DisplayName("toResponse maps all fields correctly")
    @Tag("Unit")
    void toResponse_mapsFields() {
        ProductEntity entity = ProductEntity.builder()
                .id("1")
                .name("Name")
                .description("Desc")
                .imageUrl("http://img")
                .price(new BigDecimal("9.99"))
                .category("Cat")
                .build();

        ProductResponse response = mapper.toResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getDescription(), response.getDescription());
        assertEquals(entity.getImageUrl(), response.getImageUrl());
        assertEquals(entity.getPrice(), response.getPrice());
        assertEquals(entity.getCategory(), response.getCategory());
    }
}
