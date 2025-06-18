package com.delivery.drelivery.mapper;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import com.delivery.drelivery.entity.FoodEntity;

public interface FoodMapper {

    FoodEntity toEntity(FoodRequest request);

    FoodResponse toResponse(FoodEntity entity);
}
