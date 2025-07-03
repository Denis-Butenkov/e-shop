package com.lumastyle.eshop.service.impl;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.exception.GoPayIntegrationException;
import com.lumastyle.eshop.mapper.OrderMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.OrderRepository;
import com.lumastyle.eshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {OrderServiceImpl.class})
@DisabledInAotMode
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
class OrderServiceTest {
    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @MockitoBean
    private UserService userService;

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
     * Test {@link OrderServiceImpl#createOrderAndPayment(OrderRequest)}.
     *
     * <ul>
     *   <li>Given {@link OrderRepository}.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#createOrderAndPayment(OrderRequest)}
     */
    @Test
    @DisplayName("Test createOrderAndPayment(OrderRequest); given OrderRepository")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({
            "com.lumastyle.eshop.dto.order.OrderResponse OrderServiceImpl.createOrderAndPayment(OrderRequest)"
    })
    void testCreateOrderAndPayment_givenOrderRepository() throws UnknownHostException {
        try (MockedStatic<InetAddress> mockInetAddress = mockStatic(InetAddress.class)) {

            // Arrange
            mockInetAddress
                    .when(() -> InetAddress.getAllByName(Mockito.<String>any()))
                    .thenReturn(new InetAddress[]{null});
            when(orderMapper.toEntity(Mockito.<OrderRequest>any()))
                    .thenThrow(new GoPayIntegrationException("An error occurred"));

            // Act and Assert
            assertThrows(
                    GoPayIntegrationException.class,
                    () -> orderServiceImpl.createOrderAndPayment(new OrderRequest()));
            verify(orderMapper).toEntity(isA(OrderRequest.class));
        }
    }

    /**
     * Test {@link OrderServiceImpl#createOrderAndPayment(OrderRequest)}.
     *
     * <ul>
     *   <li>Then calls {@link OrderRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#createOrderAndPayment(OrderRequest)}
     */
    @Test
    @DisplayName("Test createOrderAndPayment(OrderRequest); then calls save(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"OrderResponse OrderServiceImpl.createOrderAndPayment(OrderRequest)"})
    void testCreateOrderAndPayment_thenCallsSave() throws UnknownHostException {
        try (MockedStatic<InetAddress> mockInetAddress = mockStatic(InetAddress.class)) {

            // Arrange
            mockInetAddress
                    .when(() -> InetAddress.getAllByName(Mockito.<String>any()))
                    .thenReturn(new InetAddress[]{null});
            when(orderRepository.save(Mockito.<OrderEntity>any()))
                    .thenThrow(new GoPayIntegrationException("An error occurred"));

            OrderEntity orderEntity = getOrderEntity();
            when(orderMapper.toEntity(Mockito.<OrderRequest>any())).thenReturn(orderEntity);

            // Act and Assert
            assertThrows(
                    GoPayIntegrationException.class,
                    () -> orderServiceImpl.createOrderAndPayment(new OrderRequest()));
            verify(orderMapper).toEntity(isA(OrderRequest.class));
            verify(orderRepository).save(isA(OrderEntity.class));
        }
    }

    /**
     * Test {@link OrderServiceImpl#getUserOrders()}.
     *
     * <p>Method under test: {@link OrderServiceImpl#getUserOrders()}
     */
    @Test
    @DisplayName("Test getUserOrders()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List OrderServiceImpl.getUserOrders()"})
    void testGetUserOrders() {
        // Arrange
        when(orderRepository.findByUserId(Mockito.<String>any()))
                .thenThrow(new GoPayIntegrationException("An error occurred"));
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act and Assert
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.getUserOrders());
        verify(orderRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link OrderServiceImpl#getUserOrders()}.
     *
     * <ul>
     *   <li>Given {@link OrderRepository}.
     *   <li>Then throw {@link GoPayIntegrationException}.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#getUserOrders()}
     */
    @Test
    @DisplayName("Test getUserOrders(); given OrderRepository; then throw GoPayIntegrationException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List OrderServiceImpl.getUserOrders()"})
    void testGetUserOrders_givenOrderRepository_thenThrowGoPayIntegrationException() {
        // Arrange
        when(userService.getCurrentUserId())
                .thenThrow(new GoPayIntegrationException("An error occurred"));

        // Act and Assert
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.getUserOrders());
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link OrderServiceImpl#getUserOrders()}.
     *
     * <ul>
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#getUserOrders()}
     */
    @Test
    @DisplayName("Test getUserOrders(); then return Empty")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List OrderServiceImpl.getUserOrders()"})
    void testGetUserOrders_thenReturnEmpty() {
        // Arrange
        when(orderRepository.findByUserId(Mockito.<String>any())).thenReturn(new ArrayList<>());
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act
        List<OrderResponse> actualUserOrders = orderServiceImpl.getUserOrders();

        // Assert
        verify(orderRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
        assertTrue(actualUserOrders.isEmpty());
    }

    /**
     * Test {@link OrderServiceImpl#removeOrder(String)}.
     *
     * <ul>
     *   <li>Given {@link OrderRepository} {@link OrderRepository#deleteById(Object)} does nothing.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#removeOrder(String)}
     */
    @Test
    @DisplayName("Test removeOrder(String); given OrderRepository deleteById(Object) does nothing")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void OrderServiceImpl.removeOrder(String)"})
    void testRemoveOrder_givenOrderRepositoryDeleteByIdDoesNothing() {
        // Arrange
        doNothing().when(orderRepository).deleteById(Mockito.<String>any());

        // Act
        orderServiceImpl.removeOrder("42");

        // Assert
        verify(orderRepository).deleteById(eq("42"));
    }

    /**
     * Test {@link OrderServiceImpl#removeOrder(String)}.
     *
     * <ul>
     *   <li>Then throw {@link GoPayIntegrationException}.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#removeOrder(String)}
     */
    @Test
    @DisplayName("Test removeOrder(String); then throw GoPayIntegrationException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void OrderServiceImpl.removeOrder(String)"})
    void testRemoveOrder_thenThrowGoPayIntegrationException() {
        // Arrange
        doThrow(new GoPayIntegrationException("An error occurred"))
                .when(orderRepository)
                .deleteById(Mockito.<String>any());

        // Act and Assert
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.removeOrder("42"));
        verify(orderRepository).deleteById(eq("42"));
    }

    /**
     * Test {@link OrderServiceImpl#getOrdersOfAllUsers()}.
     *
     * <ul>
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#getOrdersOfAllUsers()}
     */
    @Test
    @DisplayName("Test getOrdersOfAllUsers(); then return Empty")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List OrderServiceImpl.getOrdersOfAllUsers()"})
    void testGetOrdersOfAllUsers_thenReturnEmpty() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<OrderResponse> actualOrdersOfAllUsers = orderServiceImpl.getOrdersOfAllUsers();

        // Assert
        verify(orderRepository).findAll();
        assertTrue(actualOrdersOfAllUsers.isEmpty());
    }

    /**
     * Test {@link OrderServiceImpl#getOrdersOfAllUsers()}.
     *
     * <ul>
     *   <li>Then throw {@link GoPayIntegrationException}.
     * </ul>
     *
     * <p>Method under test: {@link OrderServiceImpl#getOrdersOfAllUsers()}
     */
    @Test
    @DisplayName("Test getOrdersOfAllUsers(); then throw GoPayIntegrationException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List OrderServiceImpl.getOrdersOfAllUsers()"})
    void testGetOrdersOfAllUsers_thenThrowGoPayIntegrationException() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(new GoPayIntegrationException("An error occurred"));

        // Act and Assert
        assertThrows(GoPayIntegrationException.class, () -> orderServiceImpl.getOrdersOfAllUsers());
        verify(orderRepository).findAll();
    }
}
