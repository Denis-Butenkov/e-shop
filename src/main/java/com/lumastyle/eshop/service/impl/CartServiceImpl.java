package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.mapper.CartMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.service.CartService;
import com.lumastyle.eshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository repository;
    private final UserService service;
    private final CartMapper mapper;

    @Override
    public CartResponse addToCart(CartRequest request) {
        String userId = getLoggedUserId();
        CartEntity cart = repository.findByUserId(userId)
                .orElseGet(() -> new CartEntity(userId, new HashMap<>()));

        incrementItem(cart, request.getProductId());
        CartEntity saved = repository.save(cart);
        return mapper.toResponse(saved);
    }

    @Override
    public CartResponse getCart() {
        String userId = getLoggedUserId();
        CartEntity entity = repository.findByUserId(userId)
                .orElse(new CartEntity(null, userId, new HashMap<>()));
        return mapper.toResponse(entity);
    }

    @Override
    public void cleanCart() {
        String userId = getLoggedUserId();
        repository.deleteByUserId(userId);
    }

    @Override
    public CartResponse removeFromCart(CartRequest request) {
        CartEntity cart = loadCartOrThrow();
        boolean changed = decrementItem(cart, request.getProductId());
        if (changed) {
            cart = repository.save(cart);
        }
        return mapper.toResponse(cart);
    }

    // --- helper methods ---

    /**
     * Gets the ID of the currently authenticated user.
     */
    private String getLoggedUserId() {
        return service.getCurrentUserId();
    }

    /**
     * Loads the cart for the current user or throws if not found
     */
    private CartEntity loadCartOrThrow() {
        return repository.findByUserId(getLoggedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    /**
     * Increments the quantity of a given productID by 1.
     */
    private void incrementItem(CartEntity cart, String productId) {
        Map<String, Integer> items = cart.getItems();
        items.put(productId, items.getOrDefault(productId, 0) + 1);
        cart.setItems(items);
    }

    /**
     * Decrements quantity for a given productID; removes key if quantity reaches 0.
     * @return true if an item was present and changed
     */
    private boolean decrementItem(CartEntity cart, String productId) {
        Map<String, Integer> items = cart.getItems();
        if (!items.containsKey(productId)) {
            return false;
        }
        updateItemQuantity(items, productId);
        cart.setItems(items);
        return true;
    }

    /**
     * Helper to update quantity or remove the entry if it reaches zero.
     */
    private void updateItemQuantity(Map<String, Integer> items, String productId) {
        items.computeIfPresent(productId, (id, qty) -> qty > 1 ? qty - 1 : null);
    }

}

