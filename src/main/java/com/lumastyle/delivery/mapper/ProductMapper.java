package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.product.ProductRequest;
import com.lumastyle.delivery.dto.product.ProductResponse;
import com.lumastyle.delivery.entity.ProductEntity;

public interface ProductMapper {

    ProductEntity toEntity(ProductRequest request);

    ProductResponse toResponse(ProductEntity entity);
}
