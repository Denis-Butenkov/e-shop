package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.entity.ProductEntity;

public interface ProductMapper {

    /**
     * Converts a ProductRequest DTO to a ProductEntity.
     *
     * @param request the product request DTO containing product data
     * @return the mapped ProductEntity
     */
    ProductEntity toEntity(ProductRequest request);

    /**
     * Converts a ProductEntity to a ProductResponse DTO.
     *
     * @param entity the product entity retrieved from the database
     * @return the mapped ProductResponse DTO for client consumption
     */
    ProductResponse toResponse(ProductEntity entity);
}
