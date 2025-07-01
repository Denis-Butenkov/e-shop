package com.lumastyle.eshop.controller;


import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addToCart(@Valid @RequestBody CartRequest request) {
        log.info("Received request to add item to cart: {}", request);
        return service.addToCart(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart() {
        log.info("Received request to get cart contents");
        return service.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cleanCart() {
        log.info("Received request to clean cart");
        service.cleanCart();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse removeFromCart(@Valid @RequestBody CartRequest request) {
        log.info("Received request to remove item from cart: {}", request);
        return service.removeFromCart(request);
    }

}
