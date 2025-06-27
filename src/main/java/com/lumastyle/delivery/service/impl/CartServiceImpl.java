package com.lumastyle.delivery.service.impl;

import com.lumastyle.delivery.dto.cart.CartRequest;
import com.lumastyle.delivery.dto.cart.CartResponse;
import com.lumastyle.delivery.entity.CartEntity;
import com.lumastyle.delivery.mapper.CartMapper;
import com.lumastyle.delivery.repository.CartRepository;
import com.lumastyle.delivery.service.CartService;
import com.lumastyle.delivery.service.UserService;
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

        var items = cart.getItems();
        items.put(request.getFoodId(), items.getOrDefault(request.getFoodId(), 0) + 1);
        cart.setItems(items);

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
        String userId = getLoggedUserId();
        CartEntity cart = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is not found"));
        Map<String, Integer> cartItems = cart.getItems();
        if (cartItems.containsKey(request.getFoodId())) {
            cartItems.compute(request.getFoodId(), (foodId, qty) -> {
                if (qty == null) return null;
                int updated = qty - 1;
                return updated > 0 ? updated : null;
            });
            cart = repository.save(cart);
        }
        return mapper.toResponse(cart);

    }

    // === Helper methods ===

    private String getLoggedUserId() {
        return service.findByUserId();
    }
}

