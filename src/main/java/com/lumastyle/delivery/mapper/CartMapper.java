package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.cart.CartResponse;
import com.lumastyle.delivery.entity.CartEntity;

public interface CartMapper {

    CartResponse toResponse (CartEntity cart);
}
