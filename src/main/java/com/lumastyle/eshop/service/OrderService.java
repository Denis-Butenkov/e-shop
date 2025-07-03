package com.lumastyle.eshop.service;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Service interface for placing and managing customer orders.
 *
 * <p>Provides methods to create orders, initiate payments, verify payment statuses,
 * and manage existing orders for both individual users and administrators.</p>
 */
public interface OrderService {

    /**
     * Creates a new order and initiates a payment via GoPay.
     *
     * @param request the order request details
     * @return the response containing order and payment information
     */
    OrderResponse createOrderAndPayment(OrderRequest request);

    /**
     * Verifies the payment for an existing order based on GoPay callback data.
     *
     * @param paymentData the map of parameters returned by GoPay (e.g., paymentId)
     * @param status      the payment status returned by GoPay
     */
    void verifyPayment(Map<String, String> paymentData, String status);

    /**
     * Retrieves all orders for the currently authenticated user.
     *
     * @return a list of order responses for the user
     */
    List<OrderResponse> getUserOrders();

    /**
     * Removes an existing order by its identifier.
     *
     * @param orderId the unique identifier of the order to remove
     */
    void removeOrder(String orderId);

    /**
     * Retrieves orders for all users (for administrative use).
     *
     * @return a list of all user order responses
     */
    List<OrderResponse> getOrdersOfAllUsers();

    /**
     * Updates the status of an existing order (for administrative use).
     *
     * @param orderId the unique identifier of the order to update
     * @param status  the new status to set (e.g., "Paid", "Cancelled")
     * @throws ResourceNotFoundException if the order does not exist
     */
    void updateOrderStatus(String orderId, String status);
}
