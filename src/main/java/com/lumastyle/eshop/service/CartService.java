package com.lumastyle.eshop.service;

import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;

/**
 * High‐level operations for a user’s shopping cart.
 */
public interface CartService {

    /**
     * Add an item to the current user’s cart.
     *
     * @param request the product and quantity to add
     * @return the updated cart contents
     */
    CartResponse addToCart(CartRequest request);

    /**
     * Retrieve the current contents of the user’s cart.
     *
     * @return the cart contents and totals
     */
    CartResponse getCart();

    /**
     * Remove all items from the current user’s cart.
     */
    void cleanCart();

    /**
     * Remove a specific item (or quantity) from the cart.
     *
     * @param request the product and quantity to remove
     * @return the updated cart contents
     */
    CartResponse removeFromCart(CartRequest request);
}
