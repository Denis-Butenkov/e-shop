package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.mapper.OrderMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.OrderRepository;
import com.lumastyle.eshop.service.OrderService;
import com.lumastyle.eshop.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper mapper;
    private final UserService userService;

    @Value("${razorpay.key}")
    private String razorpayKey;
    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Override
    public OrderResponse createOrderAndPayment(OrderRequest request) throws RazorpayException {
        OrderEntity newOrder = mapper.toEntity(request);
        log.info("Creating new order: {}", newOrder);
        newOrder = orderRepository.save(newOrder);

        // create a payment order
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", newOrder.getAmount());
        orderRequest.put("currency", "CZK");
        orderRequest.put("payment_capture", 1);
        log.info("Creating payment order: {}", orderRequest);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        newOrder.setPaymentOrderId(razorpayOrder.get("id"));
        String loggedInUserId = userService.getCurrentUserId();
        newOrder.setUserId(loggedInUserId);
        newOrder = orderRepository.save(newOrder);
        log.info("Order created successfully: {}", newOrder);
        return mapper.toResponse(newOrder);
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        OrderEntity existingOrder = orderRepository.findByPaymentOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + razorpayOrderId));
        log.info("Order found successfully: {}", existingOrder);
        existingOrder.setPaymentStatus(status);
        existingOrder.setSignature(paymentData.get("razorpay_signature"));
        existingOrder.setPaymentId(paymentData.get("razorpay_payment_id"));
        orderRepository.save(existingOrder);
        log.info("Order updated successfully: {}", existingOrder);
        if ("paid".equalsIgnoreCase(status)) {
            cartRepository.deleteByUserId(existingOrder.getUserId());
            log.info("Cart for user {} cleared successfully", existingOrder.getUserId());
        }

    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.getCurrentUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
        log.info("Found {} orders for user {}", list.size(), loggedInUserId);
        return list.stream().map(mapper::toResponse).toList();
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
        log.info("Order with id {} removed successfully", orderId);
    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        List<OrderEntity> allOrders = orderRepository.findAll();
        log.info("Found {} orders", allOrders.size());
        return allOrders.stream().map(mapper::toResponse).toList();
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        entity.setOrderStatus(status);
        orderRepository.save(entity);
    }

}
