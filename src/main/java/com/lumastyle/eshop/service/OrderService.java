package com.lumastyle.eshop.service;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.razorpay.RazorpayException;

import java.util.List;
import java.util.Map;

/**
 * Place and manage customer orders.
 */
public interface OrderService {

    OrderResponse createOrderAndPayment(OrderRequest request) throws RazorpayException;

    void verifyPayment(Map<String, String> paymentData, String status);

    List<OrderResponse> getUserOrders();

    void removeOrder(String orderId);

    List<OrderResponse> getOrdersOfAllUsers();

    void updateOrderStatus(String orderId, String status);
}
