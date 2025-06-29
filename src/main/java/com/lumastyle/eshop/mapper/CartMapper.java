package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;

public interface CartMapper {

    /**
     * Converts a CartEntity to a CartResponse DTO for client consumption.
     *
     * @param cart the persisted cart entity
     * @return the DTO summarizing the cart contents
     */
    CartResponse toResponse (CartEntity cart);
}
