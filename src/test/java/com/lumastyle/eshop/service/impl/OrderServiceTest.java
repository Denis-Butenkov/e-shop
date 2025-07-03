package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.exception.GoPayIntegrationException;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.mapper.OrderMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.OrderRepository;
import com.lumastyle.eshop.service.EmailService;
import com.lumastyle.eshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.net.InetAddress;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderServiceImpl} ensuring correct behavior
 * under various scenarios including error conditions.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private UserService userService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    /**
     * Creates a sample OrderEntity for use in tests.
     *
     * @return a fully populated OrderEntity instance
     */
    private static OrderEntity getOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setAmount(10);
        orderEntity.setEmail("jane.doe@example.org");
        orderEntity.setGopayPaymentId("42");
        orderEntity.setGopayTransactionId("42");
        orderEntity.setId("42");
        orderEntity.setOrderStatus("Order Status");
        orderEntity.setOrderedItems(new ArrayList<>());
        orderEntity.setPaymentStatus("Payment Status");
        orderEntity.setPhoneNumber("6625550144");
        orderEntity.setUserAddress("42 Main St");
        orderEntity.setUserId("42");
        return orderEntity;
    }

    /**
     * Verifies that an exception from the mapper is propagated
     * when creating an order and payment.
     */
    @Test
    @DisplayName("createOrderAndPayment throws when mapper fails")
    @Tag("Unit")
    void testCreateOrderAndPayment_givenMapperThrows() {
        try (MockedStatic<InetAddress> mockInetAddress = mockStatic(InetAddress.class)) {
            mockInetAddress.when(() -> InetAddress.getAllByName(anyString()))
                    .thenReturn(new InetAddress[]{null});
            when(orderMapper.toEntity(any(OrderRequest.class)))
                    .thenThrow(new GoPayIntegrationException("An error occurred"));

            assertThrows(GoPayIntegrationException.class,
                    () -> orderServiceImpl.createOrderAndPayment(new OrderRequest()));

            verify(orderMapper).toEntity(any(OrderRequest.class));
        }
    }

    /**
     * Ensures that the repository save is invoked and its exception propagated
     * when saving a mapped order fails.
     */
    @Test
    @DisplayName("createOrderAndPayment calls save and propagates error")
    @Tag("Unit")
    void testCreateOrderAndPayment_thenCallsSave() {
        try (MockedStatic<InetAddress> mockInetAddress = mockStatic(InetAddress.class)) {
            mockInetAddress.when(() -> InetAddress.getAllByName(anyString()))
                    .thenReturn(new InetAddress[]{null});
            OrderEntity entity = getOrderEntity();
            when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(entity);
            when(orderRepository.save(any(OrderEntity.class)))
                    .thenThrow(new GoPayIntegrationException("Integration error"));

            assertThrows(GoPayIntegrationException.class,
                    () -> orderServiceImpl.createOrderAndPayment(new OrderRequest()));

            verify(orderMapper).toEntity(any(OrderRequest.class));
            verify(orderRepository).save(any(OrderEntity.class));
        }
    }

    /**
     * Verifies that getUserOrders propagates repository exceptions.
     */
    @Test
    @DisplayName("getUserOrders throws when repository fails")
    @Tag("Unit")
    void testGetUserOrders_throwsGoPayIntegrationException() {
        when(userService.getCurrentUserId()).thenReturn("42");
        when(orderRepository.findByUserId(anyString()))
                .thenThrow(new GoPayIntegrationException("Error"));

        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.getUserOrders());

        verify(userService).getCurrentUserId();
        verify(orderRepository).findByUserId("42");
    }

    /**
     * Ensures getUserOrders returns an empty list when no orders exist.
     */
    @Test
    @DisplayName("getUserOrders returns empty list when none")
    @Tag("Unit")
    void testGetUserOrders_thenReturnEmpty() {
        when(userService.getCurrentUserId()).thenReturn("42");
        when(orderRepository.findByUserId(anyString()))
                .thenReturn(Collections.emptyList());

        List<OrderResponse> responses = orderServiceImpl.getUserOrders();

        verify(userService).getCurrentUserId();
        verify(orderRepository).findByUserId("42");
        assertTrue(responses.isEmpty());
    }

    /**
     * Ensures removeOrder does not throw when deleteById succeeds.
     */
    @Test
    @DisplayName("removeOrder does nothing on successful delete")
    @Tag("Unit")
    void testRemoveOrder_givenRepositoryDeleteByIdDoesNothing() {
        doNothing().when(orderRepository).deleteById(anyString());
        orderServiceImpl.removeOrder("42");
        verify(orderRepository).deleteById("42");
    }

    /**
     * Verifies removeOrder propagates exceptions from deleteById.
     */
    @Test
    @DisplayName("removeOrder throws when delete fails")
    @Tag("Unit")
    void testRemoveOrder_thenThrowGoPayIntegrationException() {
        doThrow(new GoPayIntegrationException("Error"))
                .when(orderRepository).deleteById(anyString());
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.removeOrder("42"));
        verify(orderRepository).deleteById("42");
    }

    /**
     * Ensures getOrdersOfAllUsers returns an empty list when the repository is empty.
     */
    @Test
    @DisplayName("getOrdersOfAllUsers returns empty list")
    @Tag("Unit")
    void testGetOrdersOfAllUsers_thenReturnEmpty() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderResponse> responses = orderServiceImpl.getOrdersOfAllUsers();

        verify(orderRepository).findAll();
        assertTrue(responses.isEmpty());
    }

    /**
     * Verifies getOrdersOfAllUsers propagates repository exceptions.
     */
    @Test
    @DisplayName("getOrdersOfAllUsers throws when repository fails")
    @Tag("Unit")
    void testGetOrdersOfAllUsers_thenThrowGoPayIntegrationException() {
        when(orderRepository.findAll()).thenThrow(new GoPayIntegrationException("Error"));
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.getOrdersOfAllUsers());
        verify(orderRepository).findAll();
    }

    /**
     * Ensures updateOrderStatus throws when an order is not found.
     */
    @Test
    @DisplayName("updateOrderStatus throws when order not found")
    @Tag("Unit")
    void testUpdateOrderStatus_givenRepositoryReturnsEmpty_thenThrowResourceNotFoundException() {
        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderServiceImpl.updateOrderStatus("42", "foo"));
        verify(orderRepository).findById("42");
    }

    /**
     * Verifies updateOrderStatus updates the status and saves the entity.
     */
    @Test
    @DisplayName("updateOrderStatus updates and saves when order found")
    @Tag("Unit")
    void testUpdateOrderStatus_updatesAndSaves() {
        OrderEntity entity = getOrderEntity();
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(entity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(entity);

        orderServiceImpl.updateOrderStatus("42", "newStatus");

        verify(orderRepository).findById("42");
        verify(orderRepository).save(entity);
        assertEquals("newStatus", entity.getOrderStatus());
    }

    /**
     * Ensures verifyPayment sends confirmation email and clears the cart on payment success.
     */
    @Test
    @DisplayName("verifyPayment processes payment and clears cart")
    @Tag("Unit")
    void testVerifyPayment_paid_sendsEmailAndClearsCart() {
        OrderEntity order = getOrderEntity();
        when(orderRepository.findByGopayPaymentId("gp1")).thenReturn(Optional.of(order));
        Map<String, String> data = new HashMap<>();
        data.put("paymentId", "gp1");

        orderServiceImpl.verifyPayment(data, "Paid");

        verify(emailService).sendPaymentConfirmation(
                eq(order.getEmail()),
                contains(order.getId()),
                contains("objednávku"));
        verify(cartRepository).deleteByUserId(order.getUserId());
    }

    /**
     * Verifies verifyPayment throws when the order is not found.
     */
    @Test
    @DisplayName("verifyPayment throws when order not found")
    @Tag("Unit")
    void testVerifyPayment_orderNotFound_throws() {
        when(orderRepository.findByGopayPaymentId(anyString()))
                .thenReturn(Optional.empty());
        Map<String, String> data = new HashMap<>();
        data.put("paymentId", "gpX");

        assertThrows(GoPayIntegrationException.class,
                () -> orderServiceImpl.verifyPayment(data, "Paid"));
    }
}
