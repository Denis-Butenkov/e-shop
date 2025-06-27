package com.lumastyle.delivery.mapper.impl;

import com.lumastyle.delivery.dto.cart.CartResponse;
import com.lumastyle.delivery.entity.CartEntity;
import com.lumastyle.delivery.mapper.CartMapper;
import org.springframework.stereotype.Component;

@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toResponse(CartEntity cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems())
                .build();

    }
}
