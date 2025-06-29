package com.lumastyle.eshop.controller;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.service.OrderService;
import com.razorpay.RazorpayException;
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

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderAndPayment(@RequestBody OrderRequest request) throws RazorpayException {
        log.info("Received request to create a new order: {}", request);
        return service.createOrderAndPayment(request);
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public void verifyPayment(@RequestBody Map<String, String> paymentData) {
        log.info("Received request to verify a payment: {}", paymentData);
        service.verifyPayment(paymentData, "Paid");
    }

    @GetMapping
    public List<OrderResponse> getUserOrders() {
        log.info("Received request to get all orders");
        return service.getUserOrders();
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrder(@PathVariable String orderId) {
        log.info("Received request to remove an order with id: {}", orderId);
        service.removeOrder(orderId);
    }

    // Admin panel
    @GetMapping("/all")
    public List<OrderResponse> getOrdersOfAllUsers() {
        log.info("Received request to get all orders of all users");
        return service.getOrdersOfAllUsers();
    }

    @PatchMapping("/status/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrderStatus(@RequestParam String status, @PathVariable String orderId) {
        log.info("Received request to update the status of an order with id: {} to: {}", orderId, status);
        service.updateOrderStatus(orderId, status);
    }

}
