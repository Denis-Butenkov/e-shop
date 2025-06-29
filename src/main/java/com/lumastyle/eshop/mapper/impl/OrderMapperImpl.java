package com.lumastyle.eshop.mapper.impl;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.mapper.OrderMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrderEntity toEntity(OrderRequest request) {
       return OrderEntity.builder()
               .userAddress(request.getUserAddress())
               .amount(request.getAmount())
               .orderedItems(request.getOrderedItems())
               .phoneNumber(request.getPhoneNumber())
               .email(request.getEmail())
               .build();
    }

    @Override
    public OrderResponse toResponse(OrderEntity entity) {
        return OrderResponse.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .userAddress(entity.getUserAddress())
                .userId(entity.getUserId())
                .paymentOrderId(entity.getPaymentOrderId())
                .paymentStatus(entity.getPaymentStatus())
                .orderStatus(entity.getOrderStatus())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .orderedItems(entity.getOrderedItems())
                .build();
    }
}
