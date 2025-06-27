package com.lumastyle.delivery.controller;


import com.lumastyle.delivery.dto.cart.CartRequest;
import com.lumastyle.delivery.dto.cart.CartResponse;
import com.lumastyle.delivery.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addToCart(@Valid @RequestBody CartRequest request) {
        return service.addToCart(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart() {
        return service.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cleanCart() {
        service.cleanCart();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse removeFromCart(@Valid @RequestBody CartRequest request) {
        return service.removeFromCart(request);
    }

}
