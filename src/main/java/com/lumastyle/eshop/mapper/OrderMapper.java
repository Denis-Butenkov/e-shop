package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;

public interface OrderMapper {

    OrderEntity toEntity(OrderRequest request);

    OrderResponse toResponse(OrderEntity entity);
}

