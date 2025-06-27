package com.lumastyle.delivery.service;

import com.lumastyle.delivery.dto.cart.CartRequest;
import com.lumastyle.delivery.dto.cart.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest request);

    CartResponse getCart();

    void cleanCart();

    CartResponse removeFromCart(CartRequest request);
}
