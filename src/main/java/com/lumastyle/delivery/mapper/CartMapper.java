package com.lumastyle.delivery.mapper;

import com.lumastyle.delivery.dto.cart.CartResponse;
import com.lumastyle.delivery.entity.CartEntity;

public interface CartMapper {

    /**
     * Converts a CartEntity to a CartResponse DTO for client consumption.
     *
     * @param cart the persisted cart entity
     * @return the DTO summarizing the cart contents
     */
    CartResponse toResponse (CartEntity cart);
}
