package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link CartEntity} entity and {@link CartResponse} DTO.
 */
@Mapper(componentModel = "spring")
public interface CartMapper {

    /**
     * Maps a persisted {@link CartEntity} to a {@link CartResponse} DTO for client use.
     *
     * @param cart the {@link CartEntity} containing the user ID and a map of item IDs to quantities
     * @return a {@link CartResponse} DTO with cart ID, user ID, and the items with their quantities
     */
    CartResponse toResponse (CartEntity cart);
}
