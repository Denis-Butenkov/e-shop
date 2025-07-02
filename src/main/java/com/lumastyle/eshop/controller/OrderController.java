package com.lumastyle.eshop.controller;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @Operation(summary = "Create order and payment",
            description = "Creates a new order and initializes payment process.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Order created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid order data",
                    content = @Content)
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderAndPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequest.class)))
            @RequestBody OrderRequest request) {
        log.info("Received request to create a new order: {}", request);
        return service.createOrderAndPayment(request);
    }

    @Operation(summary = "Verify payment",
            description = "Verifies the payment of an existing order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Payment verified"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid payment data",
                    content = @Content)
    })
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public void verifyPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment data from provider",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class)))
            @RequestBody Map<String, String> paymentData) {
        log.info("Received request to verify a payment: {}", paymentData);
        service.verifyPayment(paymentData, "Paid");
    }

    @Operation(summary = "Get user orders",
            description = "Retrieves orders for the authenticated user.")
    @ApiResponse(responseCode = "200",
            description = "Orders list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponse.class)))
    @GetMapping
    public List<OrderResponse> getUserOrders() {
        log.info("Received request to get all orders");
        return service.getUserOrders();
    }

    @Operation(summary = "Delete order",
            description = "Deletes an order by its ID.")
    @ApiResponse(responseCode = "204",
            description = "Order deleted")
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrder(@Parameter(
            description = "ID of the order to delete",
            required = true)
                            @PathVariable String orderId) {
        log.info("Received request to remove an order with id: {}", orderId);
        service.removeOrder(orderId);
    }

    // ===== Admin panel =====

    @Operation(summary = "Get all orders (admin)",
            description = "Retrieves all user orders (admin only).")
    @ApiResponse(responseCode = "200",
            description = "All orders list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponse.class)))
    @GetMapping("/all")
    public List<OrderResponse> getOrdersOfAllUsers() {
        log.info("Received request to get all orders of all users");
        return service.getOrdersOfAllUsers();
    }

    @Operation(summary = "Update order status",
            description = "Updates the status of a specific order.")
    @ApiResponse(responseCode = "200",
            description = "Status updated")
    @PatchMapping("/status/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrderStatus(@Parameter(description = "New status value",
                                          required = true)
                                  @RequestParam String status,
                                  @Parameter(description = "ID of the order to update",
                                          required = true)
                                  @PathVariable String orderId) {
        log.info("Received request to update the status of an order with id: {} to: {}", orderId, status);
        service.updateOrderStatus(orderId, status);
    }
}
