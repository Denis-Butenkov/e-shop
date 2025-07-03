package com.lumastyle.eshop.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.dto.order.OrderResponse.OrderResponseBuilder;
import com.lumastyle.eshop.exception.GlobalExceptionHandler;
import com.lumastyle.eshop.service.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class OrderControllerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private OrderController orderController;

    @MockitoBean
    private OrderService orderService;

    /**
     * Test {@link OrderController#createOrderAndPayment(OrderRequest)}.
     *
     * <p>Method under test: {@link OrderController#createOrderAndPayment(OrderRequest)}
     */
    @Test
    @DisplayName("Test createOrderAndPayment(OrderRequest)")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"OrderResponse OrderController.createOrderAndPayment(OrderRequest)"})
    void testCreateOrderAndPayment() throws Exception {
        // Arrange
        OrderResponseBuilder orderStatusResult =
                OrderResponse.builder()
                        .amount(10)
                        .email("jane.doe@example.org")
                        .id("42")
                        .orderStatus("Order Status");
        OrderResponse buildResult =
                orderStatusResult
                        .orderedItems(new ArrayList<>())
                        .paymentOrderId("42")
                        .paymentStatus("Payment Status")
                        .phoneNumber("6625550144")
                        .userAddress("42 Main St")
                        .userId("42")
                        .build();
        when(orderService.createOrderAndPayment(Mockito.<OrderRequest>any())).thenReturn(buildResult);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(10);
        orderRequest.setEmail("jane.doe@example.org");
        orderRequest.setOrderedItems(new ArrayList<>());
        orderRequest.setPhoneNumber("6625550144");
        orderRequest.setUserAddress("42 Main St");
        String content = new ObjectMapper().writeValueAsString(orderRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string(
                                        "{\"id\":\"42\",\"userId\":\"42\",\"userAddress\":\"42 Main St\",\"phoneNumber\":\"6625550144\",\"email\":\"jane.doe@example"
                                                + ".org\",\"amount\":10,\"paymentStatus\":\"Payment Status\",\"paymentOrderId\":\"42\",\"orderStatus\":\"Order"
                                                + " Status\",\"orderedItems\":[]}"));
    }

    /**
     * Test {@link OrderController#verifyPayment(Map)}.
     *
     * <p>Method under test: {@link OrderController#verifyPayment(Map)}
     */
    @Test
    @DisplayName("Test verifyPayment(Map)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void OrderController.verifyPayment(Map)"})
    void testVerifyPayment() throws Exception {
        // Arrange
        doNothing()
                .when(orderService)
                .verifyPayment(Mockito.<Map<String, String>>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder contentTypeResult =
                MockMvcRequestBuilders.post("/api/orders/verify").contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder =
                contentTypeResult.content(objectMapper.writeValueAsString(new HashMap<>()));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test {@link OrderController#getUserOrders()}.
     *
     * <p>Method under test: {@link OrderController#getUserOrders()}
     */
    @Test
    @DisplayName("Test getUserOrders()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"java.util.List OrderController.getUserOrders()"})
    void testGetUserOrders() throws Exception {
        // Arrange
        when(orderService.getUserOrders()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/orders");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Test {@link OrderController#removeOrder(String)}.
     *
     * <p>Method under test: {@link OrderController#removeOrder(String)}
     */
    @Test
    @DisplayName("Test removeOrder(String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void OrderController.removeOrder(String)"})
    void testRemoveOrder() throws Exception {
        // Arrange
        doNothing().when(orderService).removeOrder(Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/api/orders/{orderId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Test {@link OrderController#getOrdersOfAllUsers()}.
     *
     * <ul>
     *   <li>Given {@code /api/orders/all}.
     *   <li>When formLogin.
     *   <li>Then status {@link StatusResultMatchers#isNotFound()}.
     * </ul>
     *
     * <p>Method under test: {@link OrderController#getOrdersOfAllUsers()}
     */
    @Test
    @DisplayName(
            "Test getOrdersOfAllUsers(); given '/api/orders/all'; when formLogin; then status isNotFound()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"java.util.List OrderController.getOrdersOfAllUsers()"})
    void testGetOrdersOfAllUsers_givenApiOrdersAll_whenFormLogin_thenStatusIsNotFound()
            throws Exception {
        // Arrange
        when(orderService.getOrdersOfAllUsers()).thenReturn(new ArrayList<>());
        FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test {@link OrderController#getOrdersOfAllUsers()}.
     *
     * <ul>
     *   <li>When {@link MockMvcRequestBuilders#get(String, Object[])} {@code /api/orders/all}.
     *   <li>Then status {@link StatusResultMatchers#isOk()}.
     * </ul>
     *
     * <p>Method under test: {@link OrderController#getOrdersOfAllUsers()}
     */
    @Test
    @DisplayName(
            "Test getOrdersOfAllUsers(); when get(String, Object[]) '/api/orders/all'; then status isOk()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"java.util.List OrderController.getOrdersOfAllUsers()"})
    void testGetOrdersOfAllUsers_whenGetApiOrdersAll_thenStatusIsOk() throws Exception {
        // Arrange
        when(orderService.getOrdersOfAllUsers()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/orders/all");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Test {@link OrderController#updateOrderStatus(String, String)}.
     *
     * <p>Method under test: {@link OrderController#updateOrderStatus(String, String)}
     */
    @Test
    @DisplayName("Test updateOrderStatus(String, String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void OrderController.updateOrderStatus(String, String)"})
    void testUpdateOrderStatus() throws Exception {
        // Arrange
        doNothing().when(orderService).updateOrderStatus(Mockito.<String>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.patch("/api/orders/status/{orderId}", "42").param("status", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
