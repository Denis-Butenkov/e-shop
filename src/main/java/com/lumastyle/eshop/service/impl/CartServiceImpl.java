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
        log.info("Adding item to cart for user: {}", userId);
        CartEntity cart = repository.findByUserId(userId)
                .orElseGet(() -> new CartEntity(userId, new HashMap<>()));
        log.info("Adding item to cart: {}", request);
        incrementItem(cart, request.getProductId());
        CartEntity saved = repository.save(cart);
        log.info("Item added to cart successfully: {}", saved);
        return mapper.toResponse(saved);
    }

    @Override
    public CartResponse getCart() {
        String userId = getLoggedUserId();
        log.info("Reading cart for user: {}", getLoggedUserId());
        CartEntity entity = repository.findByUserId(userId)
                .orElse(new CartEntity(null, userId, new HashMap<>()));
        log.info("Cart found successfully: {}", entity);
        return mapper.toResponse(entity);
    }

    @Override
    public void cleanCart() {
        String userId = getLoggedUserId();
        log.info("Cleaning cart for user: {}", userId);
        repository.deleteByUserId(userId);
        log.info("Cart for user: {} deleted successfully", userId);
    }

    @Override
    public CartResponse removeFromCart(CartRequest request) {
        CartEntity cart = loadCartOrThrow();
        boolean changed = decrementItem(cart, request.getProductId());
        log.info("Removing item from cart: {}", request);
        if (changed) {
            cart = repository.save(cart);
            log.info("Item removed from cart successfully: {}", cart);
        }
        return mapper.toResponse(cart);
    }

    // --- helper methods ---

    /**
     * Retrieve the ID of the currently authenticated user.
     *
     * @return the logged-in user's ID
     */
    private String getLoggedUserId() {
        log.info("Getting logged in user ID");
        return service.getCurrentUserId();
    }

    /**
     * Load the cart for the currently authenticated user.
     *
     * @return the cart entity for the logged-in user
     * @throws ResourceNotFoundException if the cart does not exist
     */
    private CartEntity loadCartOrThrow() {
        log.info("Loading cart for user: {}", getLoggedUserId());
        return repository.findByUserId(getLoggedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    /**
     * Increase the quantity of the given product by one.
     *
     * @param cart      the cart to modify
     * @param productId the ID of the product to increment
     */
    private void incrementItem(CartEntity cart, String productId) {
        log.info("Incrementing item for productID: {}", productId);
        Map<String, Integer> items = cart.getItems();
        items.put(productId, items.getOrDefault(productId, 0) + 1);
        cart.setItems(items);
    }

    /**
     * Decrease the quantity for the given product and remove it when the count reaches zero.
     *
     * @param cart      the cart to modify
     * @param productId the ID of the product to decrement
     * @return true if the cart contained the product and was updated
     */
    private boolean decrementItem(CartEntity cart, String productId) {
        log.info("Decrementing item for productID: {}", productId);
        Map<String, Integer> items = cart.getItems();
        if (!items.containsKey(productId)) {
            return false;
        }
        updateItemQuantity(items, productId);
        cart.setItems(items);
        return true;
    }

    /**
     * Update the quantity entry for the given product, removing it when it becomes zero.
     *
     * @param items     the map of product IDs to quantities
     * @param productId the ID of the product being adjusted
     */
    private void updateItemQuantity(Map<String, Integer> items, String productId) {
        items.computeIfPresent(productId, (id, qty) -> qty > 1 ? qty - 1 : null);
    }

}

