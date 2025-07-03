package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface
 * for converting between {@link ProductRequest}/{@link ProductResponse} DTOs and {@link ProductEntity} entity.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Converts a {@link ProductRequest} DTO into a {@link ProductEntity} for persistence.
     * <p>
     * The generated entity's {@code id} and {@code imageUrl} are ignored here,
     * as they will be set by the business logic.
     *
     * @param request the {@link ProductRequest} DTO containing product data (e.g., {@code name}, {@code description}, {@code price})
     * @return a new {@link ProductEntity} populated from the request DTO
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    ProductEntity toEntity(ProductRequest request);

    /**
     * Converts a {@link ProductEntity} into a {@link ProductResponse} DTO for client consumption.
     * <p>
     * Maps all fields of the entity—including its {@code id}, {@code imageUrl}, {@code name},
     * {@code description}, {@code price} and {@code category}—into the response.
     *
     * @param entity the {@link ProductEntity} retrieved from the database
     * @return a {@link ProductResponse} DTO containing all public product data
     */
    ProductResponse toResponse(ProductEntity entity);
}
