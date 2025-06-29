package com.lumastyle.eshop.mapper.impl;

import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;
import com.lumastyle.eshop.mapper.CartMapper;
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
