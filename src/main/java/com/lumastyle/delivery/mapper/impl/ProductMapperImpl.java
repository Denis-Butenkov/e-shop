package com.lumastyle.delivery.mapper.impl;

import com.lumastyle.delivery.dto.product.ProductRequest;
import com.lumastyle.delivery.dto.product.ProductResponse;
import com.lumastyle.delivery.entity.ProductEntity;
import com.lumastyle.delivery.mapper.ProductMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {
    @Override
    public ProductEntity toEntity(ProductRequest request) {
        return ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    @Override
    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .build();
    }
}
