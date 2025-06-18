package com.delivery.drelivery.mapper;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import com.delivery.drelivery.entity.FoodEntity;
import org.springframework.stereotype.Component;

@Component
public class FoodMapperImpl implements FoodMapper{
    @Override
    public FoodEntity toEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    @Override
    public FoodResponse toResponse(FoodEntity entity) {
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .build();
    }
}
