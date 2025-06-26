package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.FoodRequest;
import com.lumastyle.delivery.dto.FoodResponse;
import com.lumastyle.delivery.entity.FoodEntity;

public interface FoodMapper {

    FoodEntity toEntity(FoodRequest request);

    FoodResponse toResponse(FoodEntity entity);
}
