package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.exception.GoPayIntegrationException;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.mapper.OrderMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.OrderRepository;
import com.lumastyle.eshop.service.OrderService;
import com.lumastyle.eshop.service.UserService;
import cz.gopay.api.v3.GPClientException;
import cz.gopay.api.v3.IGPConnector;
import cz.gopay.api.v3.impl.apacheclient.HttpClientGPConnector;
import cz.gopay.api.v3.model.common.Currency;
import cz.gopay.api.v3.model.payment.BasePayment;
import cz.gopay.api.v3.model.payment.Payment;
import cz.gopay.api.v3.model.payment.PaymentFactory;
import cz.gopay.api.v3.model.payment.support.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${gopay.api.url}")
    private String gopayApiUrl;
    @Value("${gopay.client.id}")
    private String gopayClientId;
    @Value("${gopay.client.secret}")
    private String gopayClientSecret;
    @Value("${gopay.go.id}")
    private String gopayGoId;
    @Value("${gopay.callback.return-url}")
    private String returnUrl;
    @Value("${gopay.callback.notify-url}")
    private String notifyUrl;

    @Override
    public OrderResponse createOrderAndPayment(OrderRequest request) {
        OrderEntity newOrder = mapper.toEntity(request);
        log.info("Creating new order: {}", newOrder);
        newOrder = orderRepository.save(newOrder);

        try {
            log.info("Initializing GoPay connector and getting token...");
            IGPConnector connector = HttpClientGPConnector.build(gopayApiUrl);
            connector.getAppToken(gopayClientId, gopayClientSecret);

            log.info("Creating payment in GoPay...");
            // .order() expects order number, amount, currency, description
            //  amount is in the smallest currency unit
            BasePayment payment = PaymentFactory.createBasePaymentBuilder()
                    .order(newOrder.getId(), (long) newOrder.getAmount(), Currency.CZK, "Payment for an order: " + newOrder.getId())
                    .addItem("An item of the order", (long) newOrder.getAmount(), 0L, 1, ItemType.ITEM, null, null)
                    .withCallback(returnUrl, notifyUrl)
                    .inLang("cs")
                    .toEshop(Long.valueOf(gopayGoId))
                    .build();

            // Create payment in GoPay
            Payment createdPayment = connector.createPayment(payment);

            newOrder.setGopayPaymentId(String.valueOf(createdPayment.getId()));
            newOrder.setUserId(userService.getCurrentUserId());
            newOrder = orderRepository.save(newOrder);
            log.info("GoPay payment created: {}", createdPayment);
        } catch (GPClientException e) {
            log.error("Communication error with GoPay: {}", e.getMessage(), e);
            throw new GoPayIntegrationException("Integration error with GoPay", e);
        }

        return mapper.toResponse(newOrder);
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        // Expecting thet FE will return JSON = "paymentId": "...", "status": "Paid"
        String gopayPaymentId = paymentData.get("paymentId");
        OrderEntity existingOrder = orderRepository.findByGopayPaymentId(gopayPaymentId)
                .orElseThrow(() -> new GoPayIntegrationException("Order not found: " + gopayPaymentId));
        log.info("Order found successfully: {}", existingOrder);
        existingOrder.setPaymentStatus(status);
        existingOrder.setGopayTransactionId(gopayPaymentId);
        orderRepository.save(existingOrder);
        log.info("Order updated successfully: {}", existingOrder);
        if ("Paid".equalsIgnoreCase(status)) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        entity.setOrderStatus(status);
        log.info("Order {} updated with status {}", orderId, status);
        orderRepository.save(entity);
    }
}

