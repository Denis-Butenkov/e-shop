package com.lumastyle.eshop.controller;


import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart operations")
public class CartController {

    private final CartService service;

    @Operation(summary = "Add item to cart",
            description = "Adds a specified product to the current user's shopping cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Item added to cart",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid cart request",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addToCart(@Valid
                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                          description = "Cart item details (productId, quantity)",
                                          required = true,
                                          content = @Content(schema = @Schema(implementation = CartRequest.class)))
                                  @RequestBody CartRequest request) {
        log.info("Received request to add item to cart: {}", request);
        return service.addToCart(request);
    }

    @Operation(summary = "Get cart contents",
            description = "Retrieves the current user's shopping cart contents.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Cart retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class)))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart() {
        log.info("Received request to get cart contents");
        return service.getCart();
    }

    @Operation(summary = "Empty the cart",
            description = "Removes all items from the current user's cart.")
    @ApiResponse(responseCode = "204",
            description = "Cart emptied",
            content = @Content)
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cleanCart() {
        log.info("Received request to clean cart");
        service.cleanCart();
    }

    @Operation(summary = "Remove item from cart",
            description = "Decreases quantity or removes item from the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Item removed or quantity decreased",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid cart request",
                    content = @Content)
    })
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CartResponse removeFromCart(@Valid
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               description = "Cart item details to remove",
                                               required = true,
                                               content = @Content(schema = @Schema(implementation = CartRequest.class)))
                                       @RequestBody CartRequest request) {
        log.info("Received request to remove item from cart: {}", request);
        return service.removeFromCart(request);
    }

}
