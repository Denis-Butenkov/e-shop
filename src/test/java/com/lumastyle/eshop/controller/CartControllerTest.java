package com.lumastyle.eshop.controller;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.exception.GlobalExceptionHandler;
import com.lumastyle.eshop.service.CartService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CartController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class CartControllerTest {
    @Autowired
    private CartController cartController;

    @MockitoBean
    private CartService cartService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * Test {@link CartController#addToCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@code 42}.
     *   <li>When {@link CartRequest#CartRequest()} ProductId is {@code 42}.
     *   <li>Then status {@link StatusResultMatchers#isCreated()}.
     * </ul>
     *
     * <p>Method under test: {@link CartController#addToCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test addToCart(CartRequest); given '42'; when CartRequest() ProductId is '42'; then status isCreated()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"CartResponse CartController.addToCart(CartRequest)"})
    void testAddToCart_given42_whenCartRequestProductIdIs42_thenStatusIsCreated() throws Exception {
        // Arrange
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartService.addToCart(Mockito.<CartRequest>any())).thenReturn(buildResult);
        CartRequest cartRequest = new CartRequest();
        cartRequest.setProductId("42");
        String content = new ObjectMapper().writeValueAsString(cartRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string("{\"id\":\"42\",\"userId\":\"42\",\"items\":{}}"));
    }

    /**
     * Test {@link CartController#getCart()}.
     *
     * <p>Method under test: {@link CartController#getCart()}
     */
    @Test
    @DisplayName("Test getCart()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"CartResponse CartController.getCart()"})
    void testGetCart() throws Exception {
        // Arrange
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartService.getCart()).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/cart");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string("{\"id\":\"42\",\"userId\":\"42\",\"items\":{}}"));
    }

    /**
     * Test {@link CartController#cleanCart()}.
     *
     * <ul>
     *   <li>Given {@code /api/cart}.
     *   <li>When formLogin.
     *   <li>Then status {@link StatusResultMatchers#isNotFound()}.
     * </ul>
     *
     * <p>Method under test: {@link CartController#cleanCart()}
     */
    @Test
    @DisplayName("Test cleanCart(); given '/api/cart'; when formLogin; then status isNotFound()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"void CartController.cleanCart()"})
    void testCleanCart_givenApiCart_whenFormLogin_thenStatusIsNotFound() throws Exception {
        // Arrange
        doNothing().when(cartService).cleanCart();
        FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act and Assert
        MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test {@link CartController#cleanCart()}.
     *
     * <ul>
     *   <li>When {@link MockMvcRequestBuilders#delete(String, Object[])} {@code /api/cart}.
     *   <li>Then status {@link StatusResultMatchers#isNoContent()}.
     * </ul>
     *
     * <p>Method under test: {@link CartController#cleanCart()}
     */
    @Test
    @DisplayName(
            "Test cleanCart(); when delete(String, Object[]) '/api/cart'; then status isNoContent()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"void CartController.cleanCart()"})
    void testCleanCart_whenDeleteApiCart_thenStatusIsNoContent() throws Exception {
        // Arrange
        doNothing().when(cartService).cleanCart();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/cart");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Test {@link CartController#removeFromCart(CartRequest)}.
     *
     * <p>Method under test: {@link CartController#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName("Test removeFromCart(CartRequest)")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"CartResponse CartController.removeFromCart(CartRequest)"})
    void testRemoveFromCart() throws Exception {
        // Arrange
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartService.removeFromCart(Mockito.<CartRequest>any())).thenReturn(buildResult);

        CartRequest cartRequest = new CartRequest();
        cartRequest.setProductId("42");
        String content = new ObjectMapper().writeValueAsString(cartRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.patch("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string("{\"id\":\"42\",\"userId\":\"42\",\"items\":{}}"));
    }
}
