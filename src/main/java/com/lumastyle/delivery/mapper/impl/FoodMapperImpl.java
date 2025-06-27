package com.lumastyle.delivery.mapper.impl;

import com.lumastyle.delivery.dto.food.FoodRequest;
import com.lumastyle.delivery.dto.food.FoodResponse;
import com.lumastyle.delivery.entity.FoodEntity;
import com.lumastyle.delivery.mapper.FoodMapper;
import org.springframework.stereotype.Component;

@Component
public class FoodMapperImpl implements FoodMapper {
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
